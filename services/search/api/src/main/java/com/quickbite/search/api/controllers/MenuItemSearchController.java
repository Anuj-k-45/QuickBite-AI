package com.quickbite.search.api.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.search.core.features.searchitems.SearchMenuItemsQuery;
import com.quickbite.search.core.features.searchitems.SearchMenuItemsResult;

@RestController
@RequestMapping("/api/v1/search/menu-items")
public class MenuItemSearchController {

    private final Mediator mediator;

    public MenuItemSearchController(
            Mediator mediator) {

        this.mediator = mediator;
    }

    @GetMapping
    public ResponseEntity<SearchMenuItemsResult> searchMenuItems(

            @RequestParam(required = false) String q,

            @RequestParam(required = false) UUID restaurantId,

            @RequestParam(required = false) String category,

            @RequestParam(required = false) Boolean isVeg,

            @RequestParam(required = false) Boolean available,

            @RequestParam(required = false) Double minPrice,

            @RequestParam(required = false) Double maxPrice,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size) {

        SearchMenuItemsQuery query = new SearchMenuItemsQuery(
                q,
                restaurantId,
                category,
                isVeg,
                available,
                minPrice,
                maxPrice,
                page,
                size);

        SearchMenuItemsResult result = mediator.send(query);

        return ResponseEntity.ok(result);
    }
}