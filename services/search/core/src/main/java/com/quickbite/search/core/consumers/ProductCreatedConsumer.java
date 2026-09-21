package com.quickbite.search.core.consumers;

import com.quickbite.search.core.documents.MenuItemSearchDocument;
import com.quickbite.search.core.repositories.MenuItemSearchRepository;
import com.quickbite.shared.events.catalogs.ProductCreatedV1;
import com.quickbite.search.core.config.RabbitMQConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProductCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductCreatedConsumer.class);

    private final MenuItemSearchRepository repository;

    public ProductCreatedConsumer(
            MenuItemSearchRepository repository) {

        this.repository = repository;
    }

    @RabbitListener(queues = RabbitMQConfig.CATALOG_SEARCH_QUEUE)
    public void handleProductCreated(
            ProductCreatedV1 event) {

        log.info(
                "[SEARCH] Received ProductCreatedV1 for product {}",
                event.id());

        MenuItemSearchDocument document = new MenuItemSearchDocument(
                event.id(),
                event.restaurantId(),
                event.name(),
                event.description(),
                event.price(),
                event.category(),
                event.imageUrl(),
                event.isVeg(),
                event.bestseller(),
                event.active(),
                event.available());

        repository.save(document);

        log.info(
                "[SEARCH] Indexed menu item {} into Elasticsearch",
                event.id());
    }
}