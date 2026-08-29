package com.quickbite.shared.events.restaurants;

import java.io.Serializable;
import java.util.UUID;

public class RestaurantCreatedV1 implements Serializable {
    private UUID restaurantId;
    private String name;
    private String cuisineType;

    public RestaurantCreatedV1() {
    }

    public RestaurantCreatedV1(UUID restaurantId, String name, String cuisineType) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.cuisineType = cuisineType;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public String getCuisineType() {
        return cuisineType;
    }
}