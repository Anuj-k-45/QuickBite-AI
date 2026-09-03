package com.quickbite.dispatch.api.listeners;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.dispatch.core.features.assignorder.AssignDriverCommand;
import com.quickbite.shared.events.orders.OrderCreatedV1;
import com.quickbite.shared.events.orders.OrderDriverAssignedV1;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    private final Mediator mediator;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    public OrderEventListener(Mediator mediator, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate) {
        this.mediator = mediator;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "order.created.dispatch.queue", durable = "true"), exchange = @Exchange(value = "orders.events", type = "topic"), key = "orders.order.created"))
    public void consume(String rawPayload) {
        try {
            OrderCreatedV1 event = objectMapper.readValue(rawPayload, OrderCreatedV1.class);
            log.info("--- [DISPATCH] Received OrderCreatedV1 for Order ID: {} and Restaurant ID: {}",
                    event.orderId(), event.restaurantId());

            // 1. Find the nearest driver via mediator (MongoDB + Redis GEO)
            UUID assignedDriverId = mediator.send(new AssignDriverCommand(event.orderId(), event.restaurantId()));

            log.info("--- [DISPATCH] Successfully assigned Driver ID: {} to Order ID: {}", assignedDriverId,
                    event.orderId());

            // 2. Publish the assignment event back to the exchange
            OrderDriverAssignedV1 assignedEvent = new OrderDriverAssignedV1(
                    event.orderId(),
                    assignedDriverId,
                    Instant.now());

            rabbitTemplate.convertAndSend("orders.events", "order.driver.assigned", assignedEvent);
            log.info("--- [DISPATCH] Published OrderDriverAssignedV1 for Order ID: {}", event.orderId());

        } catch (Exception e) {
            log.error("[DISPATCH ERROR] Failed to assign driver for incoming order: {}", e.getMessage(), e);
        }
    }
}