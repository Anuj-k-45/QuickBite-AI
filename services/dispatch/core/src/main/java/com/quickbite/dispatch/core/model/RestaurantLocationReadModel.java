package com.quickbite.dispatch.core.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "restaurant_locations")
public class RestaurantLocationReadModel {

    @Id
    private UUID restaurantId;
    private Double latitude;
    private Double longitude;

    protected RestaurantLocationReadModel() {
    }

    public RestaurantLocationReadModel(UUID restaurantId, Double latitude, Double longitude) {
        this.restaurantId = restaurantId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}