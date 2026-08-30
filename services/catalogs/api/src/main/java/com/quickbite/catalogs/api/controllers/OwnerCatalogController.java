package com.quickbite.catalogs.api.controllers;

import com.quickbite.catalogs.core.products.model.CatalogItem;
import com.quickbite.catalogs.core.products.data.CatalogItemRepository;
import com.quickbite.catalogs.core.products.data.RestaurantRefRepository;
import com.quickbite.catalogs.core.products.model.RestaurantRef;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/items")
public class OwnerCatalogController {

    private final CatalogItemRepository catalogItemRepository;
    private final RestaurantRefRepository restaurantRefRepository;

    public OwnerCatalogController(CatalogItemRepository catalogItemRepository,
            RestaurantRefRepository restaurantRefRepository) {
        this.catalogItemRepository = catalogItemRepository;
        this.restaurantRefRepository = restaurantRefRepository;
    }

    // Helper method for authorization
    private void validateRestaurantOwnership(UUID restaurantId, UserDetails userDetails) {
        String loggedInPhone = userDetails.getUsername(); // JWT Subject is the phone number

        RestaurantRef restaurant = restaurantRefRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        if (restaurant.getOwnerPhone() == null || !restaurant.getOwnerPhone().equals(loggedInPhone)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Unauthorized: You do not own this restaurant.");
        }
    }

    @PostMapping("/{restaurantId}/menu/items")
    public ResponseEntity<CatalogItem> addMenuItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID restaurantId,
            @Valid @RequestBody CatalogItemRequest request) {

        // 1. Verify ownership using the restaurantId from the route path
        validateRestaurantOwnership(restaurantId, userDetails);

        // 2. Proceed to save item
        CatalogItem item = new CatalogItem();
        item.setId(UUID.randomUUID());
        item.setRestaurantId(restaurantId);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCategory(request.getCategory());
        item.setActive(true);
        item.setCreatedAt(Instant.now());

        CatalogItem saved = catalogItemRepository.save(item);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{restaurantId}/menu/items/{itemId}")
    public ResponseEntity<CatalogItem> updateMenuItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID restaurantId,
            @PathVariable UUID itemId,
            @Valid @RequestBody CatalogItemRequest request) {

        // 1. Verify ownership using the restaurantId from the route path
        validateRestaurantOwnership(restaurantId, userDetails);

        CatalogItem item = catalogItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Catalog item not found"));

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCategory(request.getCategory());
        item.setActive(request.isActive());

        CatalogItem updated = catalogItemRepository.save(item);
        return ResponseEntity.ok(updated);
    }

    public static class CatalogItemRequest {
        @NotBlank
        private String name;
        private String description;
        @NotNull
        private BigDecimal price;
        @NotBlank
        private String category;
        private boolean active = true;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}