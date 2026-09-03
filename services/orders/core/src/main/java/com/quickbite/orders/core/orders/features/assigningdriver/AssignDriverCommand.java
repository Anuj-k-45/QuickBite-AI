package com.quickbite.orders.core.orders.features.assigningdriver;

import java.util.UUID;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;

import jakarta.validation.constraints.NotNull;

public record AssignDriverCommand(
        @NotNull(message = "Order ID is required") UUID orderId,
        @NotNull(message = "Driver ID is required") UUID driverId) implements ICommand<Void> {
}