package com.quickbite.catalogs.api.controllers;

import com.quickbite.catalogs.core.products.model.CatalogItem;
import com.quickbite.catalogs.core.products.data.CatalogItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants")
public class PublicCatalogController {

    private final CatalogItemRepository catalogItemRepository;

    public PublicCatalogController(CatalogItemRepository catalogItemRepository) {
        this.catalogItemRepository = catalogItemRepository;
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<CatalogItem>> getRestaurantMenu(@PathVariable UUID restaurantId) {
        List<CatalogItem> items = catalogItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId);
        return ResponseEntity.ok(items);
    }
}