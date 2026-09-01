package com.quickbite.restaurants.core.features.updaterestaurant;

import java.math.BigDecimal;
import java.util.UUID;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;

public record UpdateRestaurantCommand(
                UUID id,
                String name,
                String cuisineType,
                String address,
                String description,
                String phoneNumber,
                String ownerPhone,
                String imageUrl,
                Integer deliveryTimeMinutes,
                BigDecimal costForTwo) implements ICommand<Void> {
}