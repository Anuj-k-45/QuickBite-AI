package com.quickbite.orders.core.orders.features.creatingorder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.buildingblocks.outbox.OutboxService;
import com.quickbite.orders.core.orders.data.OrderRepository;
import com.quickbite.orders.core.orders.model.Order;
import com.quickbite.orders.core.orders.model.OrderItem;
import com.quickbite.orders.core.orders.model.OrderStatus;
import com.quickbite.shared.events.orders.OrderCreatedV1;

@Service
public class CreateOrderCommandHandler implements ICommandHandler<CreateOrderCommand, CreateOrderResult> {

        private static final Logger log = LoggerFactory.getLogger(CreateOrderCommandHandler.class);

        private final OrderRepository orderRepository;
        private final OutboxService outboxService;

        public CreateOrderCommandHandler(OrderRepository orderRepository, OutboxService outboxService) {
                this.orderRepository = orderRepository;
                this.outboxService = outboxService;
        }

        @Override
        @Transactional
        public CreateOrderResult handle(CreateOrderCommand command) {
                UUID orderId = UUID.randomUUID();

                List<OrderItem> orderItems = command.items().stream()
                                .map(i -> new OrderItem(UUID.randomUUID(), i.productId(), i.productName(),
                                                i.unitPrice(), i.quantity()))
                                .collect(Collectors.toList());

                BigDecimal totalPrice = orderItems.stream()
                                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Instantiate Order matching its actual constructor signature
                Order order = new Order(orderId, command.customerId(), command.restaurantId(), OrderStatus.PENDING,
                                orderItems);
                orderRepository.save(order);

                // Queue event matching OrderCreatedV1 record signature
                List<OrderCreatedV1.OrderItemV1> eventItems = orderItems.stream()
                                .map(i -> new OrderCreatedV1.OrderItemV1(i.getProductId(), i.getProductName(),
                                                i.getUnitPrice(), i.getQuantity()))
                                .collect(Collectors.toList());

                OrderCreatedV1 event = new OrderCreatedV1(
                                orderId,
                                command.customerId(),
                                totalPrice,
                                OrderStatus.PENDING.name(),
                                eventItems,
                                Instant.now());

                outboxService.save(
                                "Order",
                                orderId,
                                OrderCreatedV1.class.getName(),
                                event);

                log.info("[CQRS WRITE] Saved Order {} and queued Outbox event", orderId);

                return new CreateOrderResult(orderId, command.customerId(), OrderStatus.PENDING.name(), totalPrice,
                                Instant.now());
        }
}