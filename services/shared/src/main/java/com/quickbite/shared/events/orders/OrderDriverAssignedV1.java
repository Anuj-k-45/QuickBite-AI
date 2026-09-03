package com.quickbite.shared.events.orders;

import java.time.Instant;
import java.util.UUID;

public class OrderDriverAssignedV1 {
    private UUID orderId;
    private UUID driverId;
    private Instant assignedAt;

    public OrderDriverAssignedV1() {
    }

    public OrderDriverAssignedV1(UUID orderId, UUID driverId, Instant assignedAt) {
        this.orderId = orderId;
        this.driverId = driverId;
        this.assignedAt = assignedAt;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getDriverId() {
        return driverId;
    }

    public void setDriverId(UUID driverId) {
        this.driverId = driverId;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }
}