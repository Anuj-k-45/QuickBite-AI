package com.quickbite.users.core.features.updatedriverlocation;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;

public record UpdateDriverLocationCommand(
        String phoneNumber,
        Double latitude,
        Double longitude) implements ICommand<Void> {
}