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
                    event.getId(),
                    event.getName(),
                    event.getCuisineType(),
                    null,
                    true,
                    null,
                    4.2,
                    30,
                    null);
            repository.save(readModel);
            System.out.println("Successfully projected restaurant to MongoDB: " + event.getName());
        } catch (Exception e) {
            System.err.println("Failed to process restaurant projection: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}