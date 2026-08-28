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
                Instant occurredOn) implements Serializable {
}