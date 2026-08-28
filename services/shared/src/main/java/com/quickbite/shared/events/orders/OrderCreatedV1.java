package com.quickbite.shared.events.orders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedV1(
                UUID orderId,
                UUID customerId,
                BigDecimal totalPrice,
                String status,
                List<OrderItemV1> items,
                Instant createdAt) {
        public record OrderItemV1(
                        UUID productId,
                        String productName,
                        BigDecimal unitPrice,
                        int quantity) {
        }
}