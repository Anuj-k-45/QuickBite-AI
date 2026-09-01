package com.quickbite.catalogs.core.products.features.updatingitem;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.catalogs.core.products.data.CatalogItemRepository;
import com.quickbite.catalogs.core.products.data.RestaurantRefRepository;
import com.quickbite.catalogs.core.products.model.CatalogItem;
import com.quickbite.catalogs.core.products.model.RestaurantRef;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class UpdateCatalogItemCommandHandler implements ICommandHandler<UpdateCatalogItemCommand, CatalogItem> {

    private final CatalogItemRepository catalogItemRepository;
    private final RestaurantRefRepository restaurantRefRepository;

    public UpdateCatalogItemCommandHandler(CatalogItemRepository catalogItemRepository,
            RestaurantRefRepository restaurantRefRepository) {
        this.catalogItemRepository = catalogItemRepository;
        this.restaurantRefRepository = restaurantRefRepository;
    }

    @Override
    public CatalogItem handle(UpdateCatalogItemCommand command) {
        RestaurantRef restaurant = restaurantRefRepository.findById(command.restaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        if (restaurant.getOwnerPhone() == null || !restaurant.getOwnerPhone().equals(command.loggedInPhone())) {
            throw new AccessDeniedException("Unauthorized: You do not own this restaurant.");
        }

        CatalogItem item = catalogItemRepository.findById(command.itemId())
                .orElseThrow(() -> new RuntimeException("Catalog item not found"));

        item.setName(command.name());
        item.setDescription(command.description());
        item.setPrice(command.price());
        item.setCategory(command.category());
        item.setActive(command.active());
        item.setImageUrl(command.imageUrl());
        item.setVeg(command.isVeg());
        item.setBestseller(command.bestseller());

        return catalogItemRepository.save(item);
    }
}