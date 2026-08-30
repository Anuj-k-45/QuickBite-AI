package com.quickbite.shared.events.restaurants;

import java.util.UUID;

public class RestaurantCreatedV1 {
    private UUID id;
    private String name;
    private String cuisineType;
    private String ownerPhone;

    public RestaurantCreatedV1() {
    }

    public RestaurantCreatedV1(UUID id, String name, String cuisineType, String ownerPhone) {
        this.id = id;
        this.name = name;
        this.cuisineType = cuisineType;
        this.ownerPhone = ownerPhone;
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
}