package com.quickbite.restaurants.core.features.updaterestaurant;

import java.util.UUID;
import com.quickbite.buildingblocks.mediator.abstractions.ICommand;

public record UpdateRestaurantCommand(
        UUID id,
        String name,
        String cuisineType,
        String address,
        String description,
        String phoneNumber,
        String ownerPhone) implements ICommand<Void> {
}