package com.quickbite.search.core.features.searchrestaurants;

import com.quickbite.buildingblocks.mediator.abstractions.IQuery;

public record SearchRestaurantsQuery(
        String query,
        String cuisine,
        Boolean open,
        Double minRating,
        Double maxCost,
        Double latitude,
        Double longitude,
        Double radius,
        String sort,
        int page,
        int size) implements IQuery<SearchRestaurantsResult> {
}