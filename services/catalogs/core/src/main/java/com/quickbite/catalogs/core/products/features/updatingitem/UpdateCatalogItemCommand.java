package com.quickbite.catalogs.core.products.features.updatingitem;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;
import com.quickbite.catalogs.core.products.model.CatalogItem;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateCatalogItemCommand(
                UUID restaurantId,
                UUID itemId,
                String name,
                String description,
                BigDecimal price,
                String category,
                boolean active,
                String loggedInPhone,
                String imageUrl,
                boolean isVeg,
                boolean bestseller) implements ICommand<CatalogItem> {
}