package com.quickbite.orders.core.orders.features.creatingorder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.buildingblocks.outbox.OutboxService;
import com.quickbite.orders.core.orders.data.OrderRepository;
import com.quickbite.orders.core.orders.model.Order;
import com.quickbite.orders.core.orders.model.OrderItem;
import com.quickbite.orders.core.orders.model.OrderStatus;
import com.quickbite.shared.events.orders.OrderCreatedV1;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class CreateOrderCommandHandler implements ICommandHandler<CreateOrderCommand, CreateOrderResult> {

    private final OrderRepository orderRepository;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    public CreateOrderCommandHandler(
            OrderRepository orderRepository,
            OutboxService outboxService,
            ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.outboxService = outboxService;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public CreateOrderResult handle(CreateOrderCommand command) {
        UUID orderId = UUID.randomUUID();

        List<OrderItem> items = command.items().stream()
                .map(i -> new OrderItem(
                        UUID.randomUUID(),
                        i.productId(),
                        i.productName(),
                        i.unitPrice(),
                        i.quantity()))
                .toList();

        Order order = new Order(
                orderId,
                command.customerId(),
                OrderStatus.PENDING,
                items);

        Order saved = orderRepository.save(order);

        List<OrderCreatedV1.OrderItemV1> eventItems = saved.getItems().stream()
                .map(i -> new OrderCreatedV1.OrderItemV1(
                        i.getProductId(),
                        i.getProductName(),
                        i.getUnitPrice(),
                        i.getQuantity()))
                .toList();

        OrderCreatedV1 event = new OrderCreatedV1(
                saved.getId(),
                saved.getCustomerId(),
                saved.getTotalPrice(),
                saved.getStatus().name(),
                eventItems,
                saved.getCreatedAt());

        try {
            String payload = objectMapper.writeValueAsString(event);
            outboxService.saveEvent("OrderCreatedV1", payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize OrderCreatedV1 event", e);
        }

        return new CreateOrderResult(
                saved.getId(),
                saved.getCustomerId(),
                saved.getStatus().name(),
                saved.getTotalPrice(),
                saved.getCreatedAt());
    }
}