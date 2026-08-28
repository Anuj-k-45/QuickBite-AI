package com.quickbite.orders.core.orders.features.creatingorder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateOrderResult(
                UUID id,
                UUID customerId,
                String status,
                BigDecimal totalPrice,
                Instant createdAt) {
}