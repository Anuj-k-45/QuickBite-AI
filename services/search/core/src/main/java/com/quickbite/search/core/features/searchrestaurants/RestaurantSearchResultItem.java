package com.quickbite.search.core.features.searchrestaurants;

import com.quickbite.search.core.documents.RestaurantSearchDocument;

public record RestaurantSearchResultItem(
        RestaurantSearchDocument restaurant,
        Double distanceKm) {
}