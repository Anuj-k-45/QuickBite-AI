package com.quickbite.restaurants.core.features.creatingrestaurant;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateRestaurantCommand(
                @NotBlank String name,
                @NotBlank String cuisineType,
                @NotBlank String address,
                @NotNull Double latitude,
                @NotNull Double longitude,
                boolean isOpen,
                String description,
                @NotBlank String phoneNumber,
                String ownerPhone, // This generates ownerPhone() accessor
                String imageUrl,
                Integer deliveryTimeMinutes,
                BigDecimal costForTwo) implements ICommand<UUID> {
}