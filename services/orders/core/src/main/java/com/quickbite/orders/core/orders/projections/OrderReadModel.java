package com.quickbite.orders.core.orders.projections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document(collection = "orders")
public record OrderReadModel(
                @Id UUID id,
                UUID customerId,
                UUID restaurantId,
                UUID driverId,
                String deliveryAddress,
                Double deliveryLatitude,
                Double deliveryLongitude,
                BigDecimal totalPrice,
                String status,
                List<OrderItemReadModel> items,
                Instant createdAt,
                Instant updatedAt) {

        public record OrderItemReadModel(
                        UUID productId,
                        String productName,
                        BigDecimal unitPrice,
                        int quantity) {
        }
}