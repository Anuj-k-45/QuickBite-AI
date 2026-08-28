package com.quickbite.catalogs.core.events;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CatalogItemCreatedEvent(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String category,
        Instant occurredOn) implements Serializable {
}