package com.quickbite.orders.core.orders.projections;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.shared.events.orders.OrderCreatedV1;
import com.quickbite.shared.events.orders.OrderDriverAssignedV1;
import com.quickbite.shared.events.orders.OrderStatusUpdatedV1;

@Component
public class OrderProjectionConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderProjectionConsumer.class);
    private final OrderReadModelRepository repository;
    private final ObjectMapper objectMapper;

    public OrderProjectionConsumer(OrderReadModelRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "orders.order-created.projection-queue", durable = "true"), exchange = @Exchange(value = "orders.events", type = "topic", durable = "true"), key = "orders.order.created"))
    public void handleOrderCreated(String messagePayload) {
        try {
            OrderCreatedV1 event = objectMapper.readValue(messagePayload, OrderCreatedV1.class);

            List<OrderReadModel.OrderItemReadModel> items = event.items().stream()
                    .map(i -> new OrderReadModel.OrderItemReadModel(
                            i.productId(),
                            i.productName(),
                            i.unitPrice(),
                            i.quantity()))
                    .toList();

            OrderReadModel readModel = new OrderReadModel(
                    event.orderId(),
                    event.customerId(),
                    event.restaurantId(),
                    null, // driverId is initially null until dispatched
                    event.deliveryAddress(),
                    event.deliveryLatitude(),
                    event.deliveryLongitude(),
                    event.totalPrice(),
                    event.status(),
                    items,
                    event.createdAt(),
                    Instant.now());

            repository.save(readModel);
            log.info("[MONGO PROJECTION] Order {} projected to MongoDB", event.orderId());
        } catch (Exception ex) {
            log.error("[MONGO PROJECTION ERROR] Failed to project order: {}", ex.getMessage(), ex);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "orders.status-updated.projection-queue", durable = "true"), exchange = @Exchange(value = "orders.events", type = "topic", durable = "true"), key = "orders.status.updated"))
    public void handleOrderStatusUpdated(String messagePayload) {
        try {
            OrderStatusUpdatedV1 event = objectMapper.readValue(messagePayload, OrderStatusUpdatedV1.class);

            OrderReadModel readModel = repository.findById(event.orderId())
                    .orElseThrow(() -> new RuntimeException("Order read model not found for ID: " + event.orderId()));

            OrderReadModel updatedReadModel = new OrderReadModel(
                    readModel.id(),
                    readModel.customerId(),
                    readModel.restaurantId(),
                    readModel.driverId(),
                    readModel.deliveryAddress(),
                    readModel.deliveryLatitude(),
                    readModel.deliveryLongitude(),
                    readModel.totalPrice(),
                    event.newStatus(),
                    readModel.items(),
                    readModel.createdAt(),
                    Instant.now());

            repository.save(updatedReadModel);
            log.info("[MONGO PROJECTION] Order {} status updated to {} in MongoDB", event.orderId(), event.newStatus());
        } catch (Exception ex) {
            log.error("[MONGO PROJECTION ERROR] Failed to update order status projection: {}", ex.getMessage(), ex);
        }
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "orders.driver-assigned.projection-queue", durable = "true"), exchange = @Exchange(value = "orders.events", type = "topic", durable = "true"), key = "order.driver.assigned"))
    public void handleOrderDriverAssigned(OrderDriverAssignedV1 event) {
        try {
            OrderReadModel readModel = repository.findById(event.getOrderId())
                    .orElseThrow(
                            () -> new RuntimeException("Order read model not found for ID: " + event.getOrderId()));

            OrderReadModel updatedReadModel = new OrderReadModel(
                    readModel.id(),
                    readModel.customerId(),
                    readModel.restaurantId(),
                    event.getDriverId(), // Updates the projected driverId
                    readModel.deliveryAddress(),
                    readModel.deliveryLatitude(),
                    readModel.deliveryLongitude(),
                    readModel.totalPrice(),
                    readModel.status(),
                    readModel.items(),
                    readModel.createdAt(),
                    Instant.now());

            repository.save(updatedReadModel);
            log.info("[MONGO PROJECTION] Order {} assigned to Driver {} in MongoDB projection", event.getOrderId(),
                    event.getDriverId());
        } catch (Exception ex) {
            log.error("[MONGO PROJECTION ERROR] Failed to update driver assignment projection: {}", ex.getMessage(),
                    ex);
        }
    }
}