package com.quickbite.buildingblocks.outbox;

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

    @Value("${quickbite.rabbitmq.catalogs-exchange:catalogs.events}")
    private String catalogsExchange;

    @Value("${quickbite.rabbitmq.catalogs-routing-key:catalogs.item.created}")
    private String catalogsRoutingKey;

    @Value("${quickbite.rabbitmq.orders-exchange:orders.events}")
    private String ordersExchange;

    @Value("${quickbite.rabbitmq.orders-routing-key:orders.order.created}")
    private String ordersRoutingKey;

    public OutboxPublisher(OutboxMessageRepository outboxRepository, RabbitTemplate rabbitTemplate) {
        this.outboxRepository = outboxRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publishPendingMessages() {
        List<OutboxMessage> messages = outboxRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        for (OutboxMessage message : messages) {
            try {
                String targetExchange;
                String targetRoutingKey;

                String eventType = message.getEventType() != null ? message.getEventType() : "";
                if (eventType.contains("Order")) {
                    targetExchange = ordersExchange;
                    targetRoutingKey = ordersRoutingKey;
                } else {
                    targetExchange = catalogsExchange;
                    targetRoutingKey = catalogsRoutingKey;
                }

                rabbitTemplate.convertAndSend(targetExchange, targetRoutingKey, message.getPayload());
                message.setStatus(OutboxStatus.PROCESSED);
                message.setProcessedAt(Instant.now());
                outboxRepository.save(message);
                log.info("[OUTBOX PUBLISHED] Sent event {} [id: {}] to exchange {} with key {}", eventType,
                        message.getId(), targetExchange, targetRoutingKey);
            } catch (Exception ex) {
                log.error("[OUTBOX ERROR] Failed to send message {}", message.getId(), ex);
                message.setStatus(OutboxStatus.FAILED);
                message.setErrorMessage(ex.getMessage());
                outboxRepository.save(message);
            }
        }
    }
}