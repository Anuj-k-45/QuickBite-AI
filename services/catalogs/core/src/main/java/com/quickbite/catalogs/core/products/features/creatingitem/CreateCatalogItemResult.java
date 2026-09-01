package com.quickbite.catalogs.core.products.features.creatingitem;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCatalogItemResult(
                UUID id,
                String name,
                BigDecimal price,
                String category,
                boolean available) {
}