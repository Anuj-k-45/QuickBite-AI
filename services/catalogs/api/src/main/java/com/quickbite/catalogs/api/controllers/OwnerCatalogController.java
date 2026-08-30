package com.quickbite.catalogs.api.controllers;

import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.catalogs.core.products.features.addingitem.AddCatalogItemCommand;
import com.quickbite.catalogs.core.products.features.updatingitem.UpdateCatalogItemCommand;
import com.quickbite.catalogs.core.products.model.CatalogItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/owner/restaurants")
public class OwnerCatalogController {

    private final Mediator mediator;

    public OwnerCatalogController(Mediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/{restaurantId}/catalog/items")
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
                loggedInPhone);

        CatalogItem saved = mediator.send(command);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{restaurantId}/catalog/items/{itemId}")
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
                loggedInPhone);

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