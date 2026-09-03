package com.quickbite.shared.events.orders;

import java.time.Instant;
import java.util.UUID;

public record OrderStatusUpdatedV1(
        UUID orderId,
        String newStatus,
        Instant updatedAt) {
}