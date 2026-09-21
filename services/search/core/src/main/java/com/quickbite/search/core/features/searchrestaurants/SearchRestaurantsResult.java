package com.quickbite.search.core.features.searchrestaurants;

import java.util.List;

public record SearchRestaurantsResult(
        List<RestaurantSearchResultItem> restaurants,
        long totalElements,
        int page,
        int size) {
}