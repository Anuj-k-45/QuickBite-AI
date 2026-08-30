package com.quickbite.restaurants.api.controllers;

import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.restaurants.core.features.gettingrestaurants.GetActiveRestaurantsQuery;
import com.quickbite.restaurants.core.features.gettingrestaurants.GetRestaurantByIdQuery;
import com.quickbite.restaurants.core.model.Restaurant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
public class PublicRestaurantController {

    private final Mediator mediator;

    public PublicRestaurantController(Mediator mediator) {
        this.mediator = mediator;
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllActiveRestaurants() {
        List<Restaurant> restaurants = mediator.send(new GetActiveRestaurantsQuery());
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable UUID id) {
        Restaurant restaurant = mediator.send(new GetRestaurantByIdQuery(id));
        return ResponseEntity.ok(restaurant);
    }
}