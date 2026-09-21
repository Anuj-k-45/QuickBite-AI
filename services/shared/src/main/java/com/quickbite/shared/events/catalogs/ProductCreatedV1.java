package com.quickbite.shared.events.catalogs;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductCreatedV1(
        UUID id,
        String name,
        BigDecimal price,
        String category,
        Instant occurredOn,

        // New fields for Search
        UUID restaurantId,
        String description,
        String imageUrl,
        boolean isVeg,
        boolean bestseller,
        boolean active,
        boolean available

) implements Serializable {

    // Backward-compatible constructor.
    // Existing code using the original 5 fields will continue to work.
    public ProductCreatedV1(
            UUID id,
            String name,
            BigDecimal price,
            String category,
            Instant occurredOn) {

        this(
                id,
                name,
                price,
                category,
                occurredOn,
                null,
                null,
                null,
                true,
                false,
                true,
                true);
    }
}