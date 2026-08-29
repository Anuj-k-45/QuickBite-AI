package com.quickbite.restaurants.core.projections;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.quickbite.shared.events.restaurants.RestaurantCreatedV1;

@Component
public class RestaurantProjectionConsumer {

    private final RestaurantReadModelRepository repository;

    public RestaurantProjectionConsumer(RestaurantReadModelRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "restaurant.created.queue")
    public void handleRestaurantCreated(RestaurantCreatedV1 event) {
        try {
            RestaurantReadModel readModel = new RestaurantReadModel(
                    event.getRestaurantId(),
                    event.getName(),
                    event.getCuisineType(),
                    null, // address can be updated or fetched if added to event later
                    true // default open status
            );
            repository.save(readModel);
            System.out.println("Successfully projected restaurant to MongoDB: " + event.getName());
        } catch (Exception e) {
            System.err.println("Failed to process restaurant projection: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}