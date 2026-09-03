package com.quickbite.restaurants.core.features.creatingrestaurant;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.restaurants.core.data.RestaurantRepository;
import com.quickbite.restaurants.core.model.Restaurant;
import com.quickbite.shared.events.restaurants.RestaurantCreatedV1;

@Component
public class CreateRestaurantCommandHandler implements ICommandHandler<CreateRestaurantCommand, UUID> {

    private final RestaurantRepository restaurantRepository;
    private final RabbitTemplate rabbitTemplate;

    public CreateRestaurantCommandHandler(RestaurantRepository restaurantRepository, RabbitTemplate rabbitTemplate) {
        this.restaurantRepository = restaurantRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public UUID handle(CreateRestaurantCommand command) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(command.name());
        restaurant.setCuisineType(command.cuisineType());
        restaurant.setAddress(command.address());
        restaurant.setLatitude(command.latitude()); // <-- Add this setter
        restaurant.setLongitude(command.longitude()); // <-- Add this setter
        restaurant.setOpen(command.isOpen());
        restaurant.setDescription(command.description());
        restaurant.setPhoneNumber(command.phoneNumber());
        restaurant.setOwnerPhone(command.ownerPhone());
        restaurant.setImageUrl(command.imageUrl());
        if (command.deliveryTimeMinutes() != null) {
            restaurant.setDeliveryTimeMinutes(command.deliveryTimeMinutes());
        }
        restaurant.setCostForTwo(command.costForTwo());

        System.out.println("... [RESTAURANT BEING SAVED IN RESTAURANT REPO]");
        Restaurant saved = restaurantRepository.save(restaurant);
        System.out.println("... [RESTAURANT SAVED SUCCESSFULLY IN RESTAURANT REPO]");

        RestaurantCreatedV1 event = new RestaurantCreatedV1(
                saved.getId(),
                saved.getName(),
                saved.getCuisineType(),
                command.ownerPhone(),
                saved.getLatitude(), // Pass latitude
                saved.getLongitude()); // Pass longitude

        rabbitTemplate.convertAndSend("restaurant.exchange", "restaurant.created", event);
        return saved.getId();
    }
}