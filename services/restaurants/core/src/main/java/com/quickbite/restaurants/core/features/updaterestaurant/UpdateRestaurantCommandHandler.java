package com.quickbite.restaurants.core.features.updaterestaurant;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.restaurants.core.data.RestaurantRepository;
import com.quickbite.restaurants.core.model.Restaurant;
import org.springframework.stereotype.Component;

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

        // Verify ownership
        if (restaurant.getOwnerPhone() != null && !restaurant.getOwnerPhone().equals(command.ownerPhone())) {
            throw new RuntimeException("Unauthorized: You do not own this restaurant");
        }

        restaurant.setName(command.name());
        restaurant.setCuisineType(command.cuisineType());
        restaurant.setAddress(command.address());

        try {
            restaurant.getClass().getMethod("setDescription", String.class).invoke(restaurant, command.description());
        } catch (Exception ignored) {
        }
        try {
            restaurant.getClass().getMethod("setPhoneNumber", String.class).invoke(restaurant, command.phoneNumber());
        } catch (Exception ignored) {
        }

        restaurantRepository.save(restaurant);
        return null;
    }
}