package com.quickbite.orders.core.orders.projections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener; // Adjust package to your orders assign driver command
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.orders.core.orders.features.assigningdriver.AssignDriverCommand;
import com.quickbite.shared.events.orders.OrderDriverAssignedV1;

@Component
public class OrderDriverAssignedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderDriverAssignedListener.class);
    private final Mediator mediator;

    public OrderDriverAssignedListener(Mediator mediator) {
        this.mediator = mediator;
    }

    @RabbitListener(queues = "order.driver.assigned.orders.queue")
    public void consume(OrderDriverAssignedV1 event) {
        try {
            log.info("--- [ORDERS] Received OrderDriverAssignedV1 for Order ID: {} with Driver ID: {}",
                    event.getOrderId(), event.getDriverId());

            mediator.send(new AssignDriverCommand(event.getOrderId(), event.getDriverId()));

            log.info("--- [ORDERS] Successfully updated order status for Order ID: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("[ORDERS ERROR] Failed to process OrderDriverAssignedV1 event: {}", e.getMessage(), e);
        }
    }
}