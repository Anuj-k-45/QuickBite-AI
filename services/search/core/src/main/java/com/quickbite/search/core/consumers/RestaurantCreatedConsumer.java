package com.quickbite.search.core.consumers;

import com.quickbite.search.core.documents.RestaurantSearchDocument;
import com.quickbite.search.core.repositories.RestaurantSearchRepository;
import com.quickbite.shared.events.restaurants.RestaurantCreatedV1;
import com.quickbite.search.core.config.RabbitMQConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Component;

@Component
public class RestaurantCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(RestaurantCreatedConsumer.class);

    private final RestaurantSearchRepository repository;

    public RestaurantCreatedConsumer(
            RestaurantSearchRepository repository) {

        this.repository = repository;
    }

    @RabbitListener(queues = RabbitMQConfig.RESTAURANT_SEARCH_QUEUE)
    public void handleRestaurantCreated(
            RestaurantCreatedV1 event) {

        log.info(
                "[SEARCH] Received RestaurantCreatedV1 for restaurant {}",
                event.getId());

        GeoPoint location = new GeoPoint(
                event.getLatitude(),
                event.getLongitude());

        RestaurantSearchDocument document = new RestaurantSearchDocument(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getCuisineType(),
                event.getAddress(),
                event.isOpen(),
                event.getImageUrl(),
                event.getRating(),
                event.getDeliveryTimeMinutes(),
                event.getCostForTwo(),
                location);

        repository.save(document);

        log.info(
                "[SEARCH] Indexed restaurant {} into Elasticsearch",
                event.getId());
    }
}