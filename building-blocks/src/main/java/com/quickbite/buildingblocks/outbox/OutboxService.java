package com.quickbite.buildingblocks.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OutboxService {

    private static final Logger log = LoggerFactory.getLogger(OutboxService.class);

    private final OutboxMessageRepository outboxMessageRepository;
    private final ObjectMapper objectMapper;

    public OutboxService(OutboxMessageRepository outboxMessageRepository, ObjectMapper objectMapper) {
        this.outboxMessageRepository = outboxMessageRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void save(String aggregateType, UUID aggregateId, String eventType, Object payload) {
        try {
            String jsonPayload = (payload instanceof String str) ? str : objectMapper.writeValueAsString(payload);
            OutboxMessage message = new OutboxMessage(
                    UUID.randomUUID(),
                    aggregateType,
                    aggregateId,
                    eventType,
                    jsonPayload);
            outboxMessageRepository.save(message);
            log.debug("[OUTBOX] Queued message {} for aggregate {}", message.getId(), aggregateId);
        } catch (Exception e) {
            log.error("[OUTBOX ERROR] Could not serialize outbox payload", e);
            throw new RuntimeException("Failed to persist outbox event", e);
        }
    }

    @Transactional
    public void saveEvent(String eventType, String payload) {
        OutboxMessage message = new OutboxMessage(eventType, payload);
        outboxMessageRepository.save(message);
    }
}