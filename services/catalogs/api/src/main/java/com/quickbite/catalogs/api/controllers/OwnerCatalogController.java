package com.quickbite.catalogs.api.controllers;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.catalogs.core.products.features.addingitem.AddCatalogItemCommand;
import com.quickbite.catalogs.core.products.features.updatingitem.UpdateCatalogItemCommand;
import com.quickbite.catalogs.core.products.model.CatalogItem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1/owner/restaurants")
@Tag(name = "Owner Catalog API", description = "Restaurant owner menu and item management")
public class OwnerCatalogController {

    private final Mediator mediator;

    public OwnerCatalogController(Mediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/{restaurantId}/catalog/items")
    @Operation(summary = "Add a menu item to a restaurant catalog")
    public ResponseEntity<CatalogItem> addMenuItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID restaurantId,
            @Valid @RequestBody CatalogItemRequest request) {

        String loggedInPhone = userDetails != null ? userDetails.getUsername() : null;

        AddCatalogItemCommand command = new AddCatalogItemCommand(
                restaurantId,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getCategory(),
                request.isActive(),
                loggedInPhone,
                request.getImageUrl(),
                request.isVeg(),
                request.isBestseller());

        CatalogItem saved = mediator.send(command);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{restaurantId}/catalog/items/{itemId}")
    @Operation(summary = "Update an existing catalog menu item")
    public ResponseEntity<CatalogItem> updateMenuItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID restaurantId,
            @PathVariable UUID itemId,
            @Valid @RequestBody CatalogItemRequest request) {

        String loggedInPhone = userDetails != null ? userDetails.getUsername() : null;

        UpdateCatalogItemCommand command = new UpdateCatalogItemCommand(
                restaurantId,
                itemId,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getCategory(),
                request.isActive(),
                loggedInPhone,
                request.getImageUrl(),
                request.isVeg(),
                request.isBestseller());

        CatalogItem updated = mediator.send(command);
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
        private String imageUrl;
        private boolean isVeg = true;
        private boolean bestseller = false;

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

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public boolean isVeg() {
            return isVeg;
        }

        public void setVeg(boolean veg) {
            isVeg = veg;
        }

        public boolean isBestseller() {
            return bestseller;
        }

        public void setBestseller(boolean bestseller) {
            this.bestseller = bestseller;
        }
    }
}