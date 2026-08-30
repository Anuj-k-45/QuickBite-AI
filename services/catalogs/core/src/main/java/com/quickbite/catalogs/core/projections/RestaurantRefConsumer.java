package com.quickbite.catalogs.core.projections;

import com.quickbite.catalogs.core.products.data.RestaurantRefRepository;
import com.quickbite.catalogs.core.products.model.RestaurantRef;
import com.quickbite.shared.events.restaurants.RestaurantCreatedV1;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RestaurantRefConsumer {

    private final RestaurantRefRepository restaurantRefRepository;

    public RestaurantRefConsumer(RestaurantRefRepository restaurantRefRepository) {
        this.restaurantRefRepository = restaurantRefRepository;
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "catalogs.restaurant.created.queue", durable = "true"), exchange = @Exchange(value = "restaurant.exchange", type = "topic"), key = "restaurant.created"))
    public void handleRestaurantCreated(RestaurantCreatedV1 event) {
        System.out.println("<<< [CATALOGS CONSUMER] Received RestaurantCreatedV1 event! ID: "
                + event.getId() + ", Owner Phone: " + event.getOwnerPhone());

        RestaurantRef ref = new RestaurantRef();
        ref.setId(event.getId());
        ref.setOwnerPhone(event.getOwnerPhone());

        restaurantRefRepository.save(ref);

        System.out.println("<<< [CATALOGS CONSUMER] Successfully saved RestaurantRef locally!");
    }
}