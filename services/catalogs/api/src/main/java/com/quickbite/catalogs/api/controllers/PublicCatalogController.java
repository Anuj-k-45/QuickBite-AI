package com.quickbite.catalogs.api.controllers;

import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.catalogs.core.products.features.gettingcatalog.GetRestaurantCatalogQuery;
import com.quickbite.catalogs.core.products.model.CatalogItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
public class PublicCatalogController {

    private final Mediator mediator;

    public PublicCatalogController(Mediator mediator) {
        this.mediator = mediator;
    }

    @GetMapping("/{restaurantId}/catalog")
    public ResponseEntity<List<CatalogItem>> getRestaurantCatalog(@PathVariable UUID restaurantId) {
        List<CatalogItem> items = mediator.send(new GetRestaurantCatalogQuery(restaurantId));
        return ResponseEntity.ok(items);
    }
}