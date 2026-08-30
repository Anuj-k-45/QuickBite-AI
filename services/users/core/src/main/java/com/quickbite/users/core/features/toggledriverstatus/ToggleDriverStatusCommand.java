package com.quickbite.users.core.features.toggledriverstatus;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;
import com.quickbite.users.core.model.DriverProfile;

public record ToggleDriverStatusCommand(
        String phoneNumber,
        boolean isOnline) implements ICommand<DriverProfile> {
}