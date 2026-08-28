package com.quickbite.orders.core.orders.projections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.shared.events.orders.OrderCreatedV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

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
                    event.totalPrice(),
                    event.status(),
                    items,
                    event.createdAt());

            repository.save(readModel);
            log.info("[MONGO PROJECTION] Order {} projected to MongoDB", event.orderId());
        } catch (Exception ex) {
            log.error("[MONGO PROJECTION ERROR] Failed to project order: {}", ex.getMessage(), ex);
        }
    }
}