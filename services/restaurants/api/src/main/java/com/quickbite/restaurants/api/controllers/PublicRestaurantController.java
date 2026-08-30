package com.quickbite.restaurants.api.controllers;

import com.quickbite.restaurants.core.model.Restaurant;
import com.quickbite.restaurants.core.data.RestaurantRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
public class PublicRestaurantController {

    private final RestaurantRepository restaurantRepository;

    public PublicRestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllActiveRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findByIsOpenTrue();
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable UUID id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        return ResponseEntity.ok(restaurant);
    }
}