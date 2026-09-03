package com.quickbite.orders.core.orders.features.updatingstatus;

import java.util.UUID;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;
import com.quickbite.orders.core.orders.model.OrderStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusCommand(
        @NotNull(message = "Order ID is required") UUID orderId,
        @NotNull(message = "New order status is required") OrderStatus newStatus) implements ICommand<Void> {
}