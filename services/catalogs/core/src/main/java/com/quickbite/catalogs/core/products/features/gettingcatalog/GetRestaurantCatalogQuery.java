package com.quickbite.catalogs.core.products.features.gettingcatalog;

import java.util.List;
import java.util.UUID;

import com.quickbite.buildingblocks.mediator.abstractions.IQuery;
import com.quickbite.catalogs.core.products.model.CatalogItem;

public record GetRestaurantCatalogQuery(UUID restaurantId) implements IQuery<List<CatalogItem>> {
}