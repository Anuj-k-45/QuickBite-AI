package com.quickbite.restaurants.core.features.creatingrestaurant;

import java.math.BigDecimal;
import java.util.UUID;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;

public record CreateRestaurantCommand(
        String name,
        String cuisineType,
        String address,
        boolean isOpen,
        String description,
        String phoneNumber,
        String ownerPhone,
        String imageUrl,
        Integer deliveryTimeMinutes,
        BigDecimal costForTwo) implements ICommand<UUID> {
}