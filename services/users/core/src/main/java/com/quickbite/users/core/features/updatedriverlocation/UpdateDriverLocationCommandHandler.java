package com.quickbite.users.core.features.updatedriverlocation;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.users.core.data.DriverProfileRepository;
import com.quickbite.users.core.data.UserRepository;
import com.quickbite.users.core.model.DriverProfile;
import com.quickbite.users.core.model.User;
import org.springframework.stereotype.Component;

@Component
public class UpdateDriverLocationCommandHandler implements ICommandHandler<UpdateDriverLocationCommand, Void> {
    private final DriverProfileRepository driverRepository;
    private final UserRepository userRepository;

    public UpdateDriverLocationCommandHandler(DriverProfileRepository driverRepository, UserRepository userRepository) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Void handle(UpdateDriverLocationCommand command) {
        User user = userRepository.findByPhoneNumber(command.phoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));
        DriverProfile profile = driverRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Driver profile not found"));
        profile.setCurrentLatitude(command.latitude());
        profile.setCurrentLongitude(command.longitude());
        driverRepository.save(profile);
        return null;
    }
}