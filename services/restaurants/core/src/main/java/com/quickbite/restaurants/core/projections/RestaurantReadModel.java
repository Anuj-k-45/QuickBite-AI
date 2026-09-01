package com.quickbite.restaurants.core.projections;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "restaurants_read")
public class RestaurantReadModel {

    @Id
    private UUID id;
    private String name;
    private String cuisineType;
    private String address;
    private boolean isOpen;
    private String imageUrl;
    private Double rating;
    private Integer deliveryTimeMinutes;
    private BigDecimal costForTwo;

    public RestaurantReadModel() {
    }

    public RestaurantReadModel(UUID id, String name, String cuisineType, String address, boolean isOpen,
            String imageUrl, Double rating, Integer deliveryTimeMinutes, BigDecimal costForTwo) {
        this.id = id;
        this.name = name;
        this.cuisineType = cuisineType;
        this.address = address;
        this.isOpen = isOpen;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.deliveryTimeMinutes = deliveryTimeMinutes;
        this.costForTwo = costForTwo;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getDeliveryTimeMinutes() {
        return deliveryTimeMinutes;
    }

    public void setDeliveryTimeMinutes(Integer deliveryTimeMinutes) {
        this.deliveryTimeMinutes = deliveryTimeMinutes;
    }

    public BigDecimal getCostForTwo() {
        return costForTwo;
    }

    public void setCostForTwo(BigDecimal costForTwo) {
        this.costForTwo = costForTwo;
    }
}