package com.quickbite.dispatch.api.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.quickbite.dispatch.core.data.RestaurantLocationRepository;
import com.quickbite.dispatch.core.model.RestaurantLocationReadModel;
import com.quickbite.shared.events.restaurants.RestaurantCreatedV1;

@Component
public class RestaurantEventListener {

    private static final Logger log = LoggerFactory.getLogger(RestaurantEventListener.class);
    private final RestaurantLocationRepository restaurantLocationRepository;

    public RestaurantEventListener(RestaurantLocationRepository restaurantLocationRepository) {
        this.restaurantLocationRepository = restaurantLocationRepository;
    }

    @RabbitListener(queues = "restaurant.created.dispatch.queue")
    public void consume(RestaurantCreatedV1 event) {
        try {
            RestaurantLocationReadModel readModel = new RestaurantLocationReadModel(
                    event.getId(),
                    event.getLatitude(),
                    event.getLongitude());

            restaurantLocationRepository.save(readModel);
            log.info("--- [DISPATCH] Synchronized restaurant location projection for ID: {}", event.getId());
        } catch (Exception e) {
            log.error("[DISPATCH ERROR] Failed to process RestaurantCreatedV1 event: {}", e.getMessage(), e);
        }
    }
}