package com.quickbite.restaurants.api.controllers;

import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.restaurants.core.features.creatingrestaurant.CreateRestaurantCommand;
import com.quickbite.restaurants.core.features.updaterestaurant.UpdateRestaurantCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
public class OwnerRestaurantController {

    private final Mediator mediator;

    public OwnerRestaurantController(Mediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<UUID> createRestaurant(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RestaurantRequest request) {

        String ownerPhone = userDetails != null ? userDetails.getUsername() : null;

        CreateRestaurantCommand command = new CreateRestaurantCommand(
                request.getName(),
                request.getCuisineType(),
                request.getAddress(),
                request.getLatitude(), // <-- Ensure this is passed
                request.getLongitude(), // <-- Ensure this is passed
                request.isOpen(),
                request.getDescription(),
                request.getPhoneNumber(),
                ownerPhone,
                request.getImageUrl(),
                request.getDeliveryTimeMinutes(),
                request.getCostForTwo());

        UUID restaurantId = mediator.send(command);
        return ResponseEntity.ok(restaurantId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateRestaurant(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id,
            @Valid @RequestBody RestaurantRequest request) {

        String ownerPhone = userDetails != null ? userDetails.getUsername() : null;

        UpdateRestaurantCommand command = new UpdateRestaurantCommand(
                id,
                request.getName(),
                request.getCuisineType(),
                request.getAddress(),
                request.getDescription(),
                request.getPhoneNumber(),
                ownerPhone,
                request.getImageUrl(),
                request.getDeliveryTimeMinutes(),
                request.getCostForTwo());

        mediator.send(command);
        return ResponseEntity.ok().build();
    }

    public static class RestaurantRequest {
        @NotBlank
        private String name;
        private String description;
        @NotBlank
        private String cuisineType;
        @NotBlank
        private String address;
        @NotNull(message = "Latitude is required")
        private Double latitude;
        @NotNull(message = "Longitude is required")
        private Double longitude;
        @NotBlank
        private String phoneNumber;
        private boolean isOpen = true;
        private String imageUrl;
        private Integer deliveryTimeMinutes = 30;
        private BigDecimal costForTwo;

        // --- Add Getters and Setters for Latitude & Longitude ---
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
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
}