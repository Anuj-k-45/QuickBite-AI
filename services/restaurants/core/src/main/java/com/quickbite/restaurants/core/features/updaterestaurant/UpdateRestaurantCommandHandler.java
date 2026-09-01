package com.quickbite.restaurants.core.features.updaterestaurant;

import org.springframework.stereotype.Component;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.restaurants.core.data.RestaurantRepository;
import com.quickbite.restaurants.core.model.Restaurant;

@Component
public class UpdateRestaurantCommandHandler implements ICommandHandler<UpdateRestaurantCommand, Void> {

    private final RestaurantRepository restaurantRepository;

    public UpdateRestaurantCommandHandler(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Void handle(UpdateRestaurantCommand command) {
        Restaurant restaurant = restaurantRepository.findById(command.id())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        if (restaurant.getOwnerPhone() != null && !restaurant.getOwnerPhone().equals(command.ownerPhone())) {
            throw new RuntimeException("Unauthorized: You do not own this restaurant");
        }

        restaurant.setName(command.name());
        restaurant.setCuisineType(command.cuisineType());
        restaurant.setAddress(command.address());
        restaurant.setDescription(command.description());
        restaurant.setPhoneNumber(command.phoneNumber());
        restaurant.setImageUrl(command.imageUrl());
        if (command.deliveryTimeMinutes() != null) {
            restaurant.setDeliveryTimeMinutes(command.deliveryTimeMinutes());
        }
        restaurant.setCostForTwo(command.costForTwo());

        restaurantRepository.save(restaurant);
        return null;
    }
}