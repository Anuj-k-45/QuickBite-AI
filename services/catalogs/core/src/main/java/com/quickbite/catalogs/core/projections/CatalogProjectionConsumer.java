package com.quickbite.catalogs.core.projections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickbite.shared.events.catalogs.ProductCreatedV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CatalogProjectionConsumer {

    private static final Logger log = LoggerFactory.getLogger(CatalogProjectionConsumer.class);

    private final CatalogReadModelRepository readModelRepository;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public CatalogProjectionConsumer(CatalogReadModelRepository readModelRepository,
            MongoTemplate mongoTemplate,
            ObjectMapper objectMapper) {
        this.readModelRepository = readModelRepository;
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${quickbite.rabbitmq.catalogs-queue:catalogs.item-created.projection-queue}")
    public void consume(String rawPayload) {
        try {
            log.info("[CQRS PROJECTION CONSUMER] Target Mongo DB: {}", mongoTemplate.getDb().getName());
            ProductCreatedV1 event = objectMapper.readValue(rawPayload, ProductCreatedV1.class);

            CatalogReadModel projection = new CatalogReadModel(
                    event.id().toString(),
                    event.name(),
                    "",
                    event.price(),
                    event.category(),
                    true,
                    event.occurredOn() != null ? event.occurredOn() : Instant.now());

            CatalogReadModel saved = readModelRepository.save(projection);
            log.info(
                    "[CQRS PROJECTION CONSUMER] Successfully saved doc id={} to MongoDB collection: catalog_read_models",
                    saved.getId());
        } catch (Exception e) {
            log.error("[CQRS PROJECTION ERROR] Consumer failed to write to MongoDB: {}", e.getMessage(), e);
        }
    }
}