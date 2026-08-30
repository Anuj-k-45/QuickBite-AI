package com.quickbite.restaurants.core.features.gettingrestaurants;

import com.quickbite.buildingblocks.mediator.abstractions.IQuery;
import com.quickbite.restaurants.core.model.Restaurant;
import java.util.UUID;

public record GetRestaurantByIdQuery(UUID id) implements IQuery<Restaurant> {
}