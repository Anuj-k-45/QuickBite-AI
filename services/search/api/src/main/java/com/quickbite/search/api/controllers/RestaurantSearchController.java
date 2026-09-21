package com.quickbite.search.api.controllers;

import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.search.core.features.searchrestaurants.SearchRestaurantsQuery;
import com.quickbite.search.core.features.searchrestaurants.SearchRestaurantsResult;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search/restaurants")
public class RestaurantSearchController {

    private final Mediator mediator;

    public RestaurantSearchController(Mediator mediator) {
        this.mediator = mediator;
    }

    @GetMapping
    public ResponseEntity<SearchRestaurantsResult> searchRestaurants(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Boolean open,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxCost,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radius,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        SearchRestaurantsQuery query = new SearchRestaurantsQuery(
                q,
                cuisine,
                open,
                minRating,
                maxCost,
                latitude,
                longitude,
                radius,
                sort,
                page,
                size);

        SearchRestaurantsResult result = mediator.send(query);

        return ResponseEntity.ok(result);
    }
}