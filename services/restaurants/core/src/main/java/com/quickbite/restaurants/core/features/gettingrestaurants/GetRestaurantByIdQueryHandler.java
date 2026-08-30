package com.quickbite.restaurants.core.features.gettingrestaurants;

import com.quickbite.buildingblocks.mediator.abstractions.IQueryHandler;
import com.quickbite.restaurants.core.data.RestaurantRepository;
import com.quickbite.restaurants.core.model.Restaurant;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class GetRestaurantByIdQueryHandler implements IQueryHandler<GetRestaurantByIdQuery, Restaurant> {

    private final RestaurantRepository restaurantRepository;

    public GetRestaurantByIdQueryHandler(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Restaurant handle(GetRestaurantByIdQuery query) {
        return restaurantRepository.findById(query.id())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }
}