package com.quickbite.shared.events.restaurants;

import java.util.UUID;

public class RestaurantCreatedV1 {
    private UUID id;
    private String name;
    private String cuisineType;
    private String ownerPhone;
    private Double latitude;
    private Double longitude;

    public RestaurantCreatedV1() {
    }

    public RestaurantCreatedV1(UUID id, String name, String cuisineType, String ownerPhone, Double latitude,
            Double longitude) {
        this.id = id;
        this.name = name;
        this.cuisineType = cuisineType;
        this.ownerPhone = ownerPhone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}