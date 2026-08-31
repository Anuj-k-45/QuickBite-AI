package com.quickbite.catalogs.core.products.features.creatingitem;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.buildingblocks.outbox.OutboxService;
import com.quickbite.catalogs.core.products.data.CatalogItemRepository;
import com.quickbite.catalogs.core.products.model.CatalogItem;
import com.quickbite.shared.events.catalogs.ProductCreatedV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class CreateCatalogItemCommandHandler
                implements ICommandHandler<CreateCatalogItemCommand, CreateCatalogItemResult> {

        private static final Logger log = LoggerFactory.getLogger(CreateCatalogItemCommandHandler.class);

        private final CatalogItemRepository catalogItemRepository;
        private final OutboxService outboxService;

        public CreateCatalogItemCommandHandler(CatalogItemRepository catalogItemRepository,
                        OutboxService outboxService) {
                this.catalogItemRepository = catalogItemRepository;
                this.outboxService = outboxService;
        }

        @Override
        @Transactional
        public CreateCatalogItemResult handle(CreateCatalogItemCommand command) {
                UUID itemId = UUID.randomUUID();

                // 1. Persist JPA Entity (Write Side using setters)
                CatalogItem item = new CatalogItem();
                item.setId(itemId);
                item.setName(command.name());
                item.setDescription(command.description());
                item.setPrice(command.price());
                item.setCategory(command.category());
                item.setActive(true);
                item.setCreatedAt(Instant.now());

                catalogItemRepository.save(item);

                // 2. Queue Domain Event to Outbox
                ProductCreatedV1 event = new ProductCreatedV1(
                                itemId,
                                command.name(),
                                command.price(),
                                command.category(),
                                Instant.now());

                outboxService.save(
                                "CatalogItem",
                                itemId,
                                ProductCreatedV1.class.getName(),
                                event);

                log.info("[CQRS WRITE] Saved CatalogItem {} and queued Outbox event", itemId);
                return new CreateCatalogItemResult(itemId, item.getName(), item.getPrice(), item.getCategory(),
                                item.isActive());
        }
}