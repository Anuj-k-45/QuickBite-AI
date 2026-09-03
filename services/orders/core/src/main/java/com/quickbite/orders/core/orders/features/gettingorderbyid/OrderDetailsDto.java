package com.quickbite.orders.core.orders.features.gettingorderbyid;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderDetailsDto(
                UUID id,
                UUID customerId,
                UUID restaurantId,
                String deliveryAddress,
                Double deliveryLatitude,
                Double deliveryLongitude,
                String status,
                BigDecimal totalPrice,
                List<OrderItemDto> items,
                Instant createdAt) {
        public record OrderItemDto(
                        UUID productId,
                        String productName,
                        BigDecimal unitPrice,
                        int quantity,
                        BigDecimal subtotal) {
        }
}