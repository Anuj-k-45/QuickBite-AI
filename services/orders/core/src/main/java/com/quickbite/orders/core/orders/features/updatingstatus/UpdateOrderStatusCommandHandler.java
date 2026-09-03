package com.quickbite.orders.core.orders.features.updatingstatus;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.buildingblocks.outbox.OutboxService;
import com.quickbite.orders.core.orders.data.OrderRepository;
import com.quickbite.orders.core.orders.model.Order;
import com.quickbite.shared.events.orders.OrderStatusUpdatedV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class UpdateOrderStatusCommandHandler implements ICommandHandler<UpdateOrderStatusCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(UpdateOrderStatusCommandHandler.class);
    private final OrderRepository orderRepository;
    private final OutboxService outboxService;

    public UpdateOrderStatusCommandHandler(OrderRepository orderRepository, OutboxService outboxService) {
        this.orderRepository = orderRepository;
        this.outboxService = outboxService;
    }

    @Override
    @Transactional
    public Void handle(UpdateOrderStatusCommand command) {
        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + command.orderId()));

        order.setStatus(command.newStatus());
        orderRepository.save(order);

        // Create and save event to Outbox for RabbitMQ publishing
        OrderStatusUpdatedV1 event = new OrderStatusUpdatedV1(
                order.getId(),
                order.getStatus().name(),
                Instant.now());

        outboxService.save(
                "Order",
                order.getId(), // aggregateId (UUID)
                OrderStatusUpdatedV1.class.getName(), // eventType (String used by OutboxPublisher to map routing keys)
                event);

        log.info("[ORDER STATUS] Order {} status updated to {} and event queued in Outbox", command.orderId(),
                command.newStatus());
        return null;
    }
}