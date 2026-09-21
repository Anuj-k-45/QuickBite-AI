package com.quickbite.restaurants.core.features.creatingrestaurant;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.buildingblocks.outbox.OutboxService;
import com.quickbite.restaurants.core.data.RestaurantRepository;
import com.quickbite.restaurants.core.model.Restaurant;
import com.quickbite.shared.events.restaurants.RestaurantCreatedV1;

@Component
public class CreateRestaurantCommandHandler
        implements ICommandHandler<CreateRestaurantCommand, UUID> {

    private final RestaurantRepository restaurantRepository;
    private final OutboxService outboxService;

    public CreateRestaurantCommandHandler(
            RestaurantRepository restaurantRepository,
            OutboxService outboxService) {

        this.restaurantRepository = restaurantRepository;
        this.outboxService = outboxService;
    }

    @Override
    @Transactional
    public UUID handle(CreateRestaurantCommand command) {

            System.out.println(
                            "========== RESTAURANT CREATE DEBUG ==========");
            System.out.println("Restaurant name: " + command.name());
            System.out.println("Command isOpen: " + command.isOpen());
            System.out.println("==============================================");

        Restaurant restaurant = new Restaurant();

        restaurant.setName(command.name());
        restaurant.setCuisineType(command.cuisineType());
        restaurant.setAddress(command.address());

        restaurant.setLatitude(command.latitude());
        restaurant.setLongitude(command.longitude());

        restaurant.setOpen(command.isOpen());
        restaurant.setDescription(command.description());
        restaurant.setPhoneNumber(command.phoneNumber());
        restaurant.setOwnerPhone(command.ownerPhone());
        restaurant.setImageUrl(command.imageUrl());

        if (command.deliveryTimeMinutes() != null) {
            restaurant.setDeliveryTimeMinutes(
                    command.deliveryTimeMinutes());
        }

        restaurant.setCostForTwo(command.costForTwo());

        System.out.println(
                "... [RESTAURANT BEING SAVED IN RESTAURANT REPO]");

        Restaurant saved = restaurantRepository.save(restaurant);

        System.out.println(
                "... [RESTAURANT SAVED SUCCESSFULLY IN RESTAURANT REPO]");

        RestaurantCreatedV1 event = new RestaurantCreatedV1(
                // Existing fields
                saved.getId(),
                saved.getName(),
                saved.getCuisineType(),
                saved.getOwnerPhone(),
                saved.getLatitude(),
                saved.getLongitude(),

                // New Search fields
                saved.getDescription(),
                saved.getAddress(),
                saved.isOpen(),
                saved.getImageUrl(),
                saved.getRating(),
                saved.getDeliveryTimeMinutes(),
                saved.getCostForTwo());

        /*
         * Store the event in the Outbox table.
         *
         * The restaurant and this outbox message are
         * persisted in the SAME database transaction.
         */
        outboxService.save(
                "Restaurant",
                saved.getId(),
                RestaurantCreatedV1.class.getName(),
                event);

        return saved.getId();
    }
}