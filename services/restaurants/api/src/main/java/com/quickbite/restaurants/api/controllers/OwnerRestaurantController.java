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
                request.isOpen(),
                request.getDescription(),
                request.getPhoneNumber(),
                ownerPhone);

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
                ownerPhone);

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
        @NotBlank
        private String phoneNumber;
        private boolean isOpen = true;

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
    }
}