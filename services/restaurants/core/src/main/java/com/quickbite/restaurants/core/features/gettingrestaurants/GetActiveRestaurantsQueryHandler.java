package com.quickbite.restaurants.core.features.gettingrestaurants;

import com.quickbite.buildingblocks.mediator.abstractions.IQueryHandler;
import com.quickbite.restaurants.core.data.RestaurantRepository;
import com.quickbite.restaurants.core.model.Restaurant;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class GetActiveRestaurantsQueryHandler implements IQueryHandler<GetActiveRestaurantsQuery, List<Restaurant>> {

    private final RestaurantRepository restaurantRepository;

    public GetActiveRestaurantsQueryHandler(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public List<Restaurant> handle(GetActiveRestaurantsQuery query) {
        return restaurantRepository.findByIsOpenTrue();
    }
}