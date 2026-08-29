package com.quickbite.restaurants.core.features.creatingrestaurant;

import java.util.UUID;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;

public record CreateRestaurantCommand(
        String name,
        String cuisineType,
        String address,
        boolean isOpen) implements ICommand<UUID> {
}