package com.quickbite.users.core.features.registerdriver;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;
import com.quickbite.users.core.model.DriverProfile;

public record RegisterDriverCommand(
        String phoneNumber,
        String vehicleType,
        String vehicleNumber,
        String licenseNumber) implements ICommand<DriverProfile> {
}