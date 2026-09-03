package com.quickbite.users.core.features.updatedriverlocation;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.users.core.data.DriverProfileRepository;
import com.quickbite.users.core.data.UserRepository;
import com.quickbite.users.core.model.DriverProfile;
import com.quickbite.users.core.model.User;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class UpdateDriverLocationCommandHandler implements ICommandHandler<UpdateDriverLocationCommand, Void> {

    private final DriverProfileRepository driverRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    public UpdateDriverLocationCommandHandler(
            DriverProfileRepository driverRepository,
            UserRepository userRepository,
            StringRedisTemplate redisTemplate) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Void handle(UpdateDriverLocationCommand command) {
        User user = userRepository.findByPhoneNumber(command.phoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        DriverProfile profile = driverRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Driver profile not found"));

        // 1. Update relational DB (for audit/persistence)
        profile.setCurrentLatitude(command.latitude());
        profile.setCurrentLongitude(command.longitude());
        driverRepository.save(profile);

        // 2. CRITICAL: Instantly update Redis geospatial index for high-speed dispatch
        // & tracking
        // Key: "active:drivers" | Value: driver's ID string | Coordinates: (Longitude,
        // Latitude)
        // Note: Redis GEOADD takes Longitude *first*, then Latitude!
        String redisKey = "active:drivers";
        redisTemplate.opsForGeo().add(
                redisKey,
                new Point(command.longitude(), command.latitude()),
                profile.getId().toString());

        return null;
    }
}