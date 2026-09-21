package com.quickbite.search.core.features.searchitems;

import java.util.UUID;

import com.quickbite.buildingblocks.mediator.abstractions.IQuery;

public record SearchMenuItemsQuery(
        String query,
        UUID restaurantId,
        String category,
        Boolean isVeg,
        Boolean available,
        Double minPrice,
        Double maxPrice,
        int page,
        int size) implements IQuery<SearchMenuItemsResult> {
}