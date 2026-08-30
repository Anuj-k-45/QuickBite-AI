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
        // 1. Map command to entity
        Restaurant restaurant = new Restaurant();
        restaurant.setName(command.name());
        restaurant.setCuisineType(command.cuisineType());
        restaurant.setAddress(command.address());
        restaurant.setActive(command.isOpen());

        try {
            restaurant.getClass().getMethod("setDescription", String.class).invoke(restaurant, command.description());
        } catch (Exception ignored) {
        }
        try {
            restaurant.getClass().getMethod("setPhoneNumber", String.class).invoke(restaurant, command.phoneNumber());
        } catch (Exception ignored) {
        }
        try {
            restaurant.getClass().getMethod("setOwnerPhone", String.class).invoke(restaurant, command.ownerPhone());
        } catch (Exception ignored) {
        }

        // 2. Save to database
        System.out.println("... [RESTAURANT BEING SAVED IN RESTAURANT REPO]");
        Restaurant saved = restaurantRepository.save(restaurant);
        System.out.println("... [RESTAURANT SAVED SUCCESSFULLY IN RESTAURANT REPO]");

        // 3. Publish RabbitMQ Event
        RestaurantCreatedV1 event = new RestaurantCreatedV1(
                saved.getId(),
                saved.getName(),
                saved.getCuisineType(),
                command.ownerPhone());

        System.out.println(">>> [RESTAURANT PRODUCER] Publishing RestaurantCreatedV1 event for Restaurant ID: "
                + saved.getId() + " with Owner Phone: " + command.ownerPhone());

        rabbitTemplate.convertAndSend("restaurant.exchange", "restaurant.created", event);

        System.out.println(">>> [RESTAURANT PRODUCER] Event successfully sent to RabbitMQ exchange!");

        return saved.getId();
    }
}