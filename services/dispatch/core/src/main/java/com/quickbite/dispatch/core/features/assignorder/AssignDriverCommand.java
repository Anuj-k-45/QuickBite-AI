package com.quickbite.dispatch.core.features.assignorder;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;
import java.util.UUID;

public record AssignDriverCommand(
    UUID orderId,
    UUID restaurantId
) implements ICommand<UUID> {
}