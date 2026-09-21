package com.quickbite.catalogs.core.products.features.addingitem;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.buildingblocks.outbox.OutboxService;
import com.quickbite.catalogs.core.products.data.CatalogItemRepository;
import com.quickbite.catalogs.core.products.data.RestaurantRefRepository;
import com.quickbite.catalogs.core.products.model.CatalogItem;
import com.quickbite.catalogs.core.products.model.RestaurantRef;
import com.quickbite.shared.events.catalogs.ProductCreatedV1;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Component
public class AddCatalogItemCommandHandler
        implements ICommandHandler<AddCatalogItemCommand, CatalogItem> {

    private final CatalogItemRepository catalogItemRepository;
    private final RestaurantRefRepository restaurantRefRepository;
    private final OutboxService outboxService;

    public AddCatalogItemCommandHandler(
            CatalogItemRepository catalogItemRepository,
            RestaurantRefRepository restaurantRefRepository,
            OutboxService outboxService) {

        this.catalogItemRepository = catalogItemRepository;
        this.restaurantRefRepository = restaurantRefRepository;
        this.outboxService = outboxService;
    }

    @Override
    @Transactional
    public CatalogItem handle(AddCatalogItemCommand command) {

        RestaurantRef restaurant = restaurantRefRepository.findById(command.restaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        if (restaurant.getOwnerPhone() == null
                || !restaurant.getOwnerPhone()
                        .equals(command.loggedInPhone())) {

            throw new AccessDeniedException(
                    "Unauthorized: You do not own this restaurant.");
        }

        CatalogItem item = new CatalogItem();

        item.setId(UUID.randomUUID());
        item.setRestaurantId(command.restaurantId());
        item.setName(command.name());
        item.setDescription(command.description());
        item.setPrice(command.price());
        item.setCategory(command.category());
        item.setActive(command.active());
        item.setCreatedAt(Instant.now());
        item.setImageUrl(command.imageUrl());
        item.setVeg(command.isVeg());
        item.setBestseller(command.bestseller());

        CatalogItem savedItem = catalogItemRepository.save(item);

        ProductCreatedV1 event = new ProductCreatedV1(
                savedItem.getId(),
                savedItem.getName(),
                savedItem.getPrice(),
                savedItem.getCategory(),
                savedItem.getCreatedAt(),

                // New Search fields
                savedItem.getRestaurantId(),
                savedItem.getDescription(),
                savedItem.getImageUrl(),
                savedItem.isVeg(),
                savedItem.isBestseller(),
                savedItem.isActive(),
                savedItem.isAvailable());

        outboxService.save(
                "CatalogItem",
                savedItem.getId(),
                ProductCreatedV1.class.getName(),
                event);

        return savedItem;
    }
}