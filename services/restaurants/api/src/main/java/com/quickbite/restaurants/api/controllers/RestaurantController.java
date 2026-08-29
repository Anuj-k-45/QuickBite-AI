package com.quickbite.restaurants.api.controllers;

import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.restaurants.core.data.RestaurantRepository;
import com.quickbite.restaurants.core.features.creatingrestaurant.CreateRestaurantCommand;
import com.quickbite.restaurants.core.model.Restaurant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;
    private final Mediator mediator;

    public RestaurantController(RestaurantRepository restaurantRepository, Mediator mediator) {
        this.restaurantRepository = restaurantRepository;
        this.mediator = mediator;
    }

    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable UUID id) {
        return restaurantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public UUID createRestaurant(@RequestBody CreateRestaurantCommand command) {
        return mediator.send(command);
    }
}