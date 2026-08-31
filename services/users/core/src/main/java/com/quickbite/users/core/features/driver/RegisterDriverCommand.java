package com.quickbite.users.core.features.driver;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;
import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.users.core.data.DriverProfileRepository;
import com.quickbite.users.core.data.UserRepository;
import com.quickbite.users.core.model.DriverProfile;
import com.quickbite.users.core.model.User;
import org.springframework.stereotype.Component;

public record RegisterDriverCommand(
        String phoneNumber,
        String vehicleType,
        String vehicleNumber,
        String licenseNumber) implements ICommand<DriverProfile> {
}

@Component
class RegisterDriverCommandHandler implements ICommandHandler<RegisterDriverCommand, DriverProfile> {

    private final DriverProfileRepository driverRepository;
    private final UserRepository userRepository;

    public RegisterDriverCommandHandler(DriverProfileRepository driverRepository, UserRepository userRepository) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DriverProfile handle(RegisterDriverCommand command) {
        User user = userRepository.findByPhoneNumber(command.phoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        DriverProfile profile = driverRepository.findByUserId(user.getId())
                .orElse(new DriverProfile());

        profile.setUserId(user.getId());
        profile.setVehicleType(command.vehicleType());
        profile.setVehicleNumber(command.vehicleNumber());
        profile.setLicenseNumber(command.licenseNumber());
        profile.setVerified(false);

        return driverRepository.save(profile);
    }
}