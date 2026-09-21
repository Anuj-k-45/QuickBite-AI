package com.quickbite.buildingblocks.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxMessageRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Value("${quickbite.rabbitmq.catalogs-exchange:catalogs.events}")
    private String catalogsExchange;

    @Value("${quickbite.rabbitmq.catalogs-routing-key:catalogs.item.created}")
    private String catalogsRoutingKey;

    @Value("${quickbite.rabbitmq.orders-exchange:orders.events}")
    private String ordersExchange;

    @Value("${quickbite.rabbitmq.orders-routing-key:orders.order.created}")
    private String ordersRoutingKey;

    @Value("${quickbite.rabbitmq.restaurant-exchange:restaurant.exchange}")
    private String restaurantExchange;

    @Value("${quickbite.rabbitmq.restaurant-routing-key:restaurant.created}")
    private String restaurantRoutingKey;

    public OutboxPublisher(
            OutboxMessageRepository outboxRepository,
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper) {

        this.outboxRepository = outboxRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publishPendingMessages() {

        List<OutboxMessage> messages = outboxRepository.findByStatusOrderByCreatedAtAsc(
                OutboxStatus.PENDING);

        for (OutboxMessage message : messages) {

            try {

                String targetExchange;
                String targetRoutingKey;

                String eventType = message.getEventType() != null
                        ? message.getEventType()
                        : "";

                /*
                 * Determine the destination based on
                 * the event type.
                 */
                if (eventType.contains("Order")) {

                    targetExchange = ordersExchange;

                    if (eventType.contains("OrderStatusUpdatedV1")) {
                        targetRoutingKey = "orders.status.updated";
                    } else {
                        targetRoutingKey = ordersRoutingKey;
                    }

                } else if (eventType.contains("Restaurant")) {

                    targetExchange = restaurantExchange;
                    targetRoutingKey = restaurantRoutingKey;

                } else {

                    targetExchange = catalogsExchange;
                    targetRoutingKey = catalogsRoutingKey;
                }

                /*
                 * The Outbox stores event payloads as JSON strings.
                 *
                 * We must convert that JSON back into the actual
                 * event object before sending it through RabbitMQ.
                 *
                 * Otherwise RabbitMQ/Jackson sees the payload as:
                 *
                 * java.lang.String
                 *
                 * instead of:
                 *
                 * RestaurantCreatedV1
                 * ProductCreatedV1
                 * OrderCreatedV1
                 * etc.
                 */
                Class<?> eventClass = Class.forName(eventType);

                Object event = objectMapper.readValue(
                        message.getPayload(),
                        eventClass);

                rabbitTemplate.convertAndSend(
                        targetExchange,
                        targetRoutingKey,
                        event);

                message.setStatus(OutboxStatus.PROCESSED);
                message.setProcessedAt(Instant.now());

                outboxRepository.save(message);

                log.info(
                        "[OUTBOX PUBLISHED] Sent event {} [id: {}] "
                                + "to exchange {} with key {}",
                        eventType,
                        message.getId(),
                        targetExchange,
                        targetRoutingKey);

            } catch (Exception ex) {

                log.error(
                        "[OUTBOX ERROR] Failed to send message {}",
                        message.getId(),
                        ex);

                message.setStatus(OutboxStatus.FAILED);
                message.setErrorMessage(ex.getMessage());

                outboxRepository.save(message);
            }
        }
    }
}