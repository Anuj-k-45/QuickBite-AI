package com.quickbite.catalogs.core.products.features.addingitem;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;
import com.quickbite.catalogs.core.products.model.CatalogItem;
import java.math.BigDecimal;
import java.util.UUID;

public record AddCatalogItemCommand(
        UUID restaurantId,
        String name,
        String description,
        BigDecimal price,
        String category,
        boolean active,
        String loggedInPhone) implements ICommand<CatalogItem> {
}