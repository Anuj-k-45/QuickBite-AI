package com.quickbite.users.api.controllers;

import com.quickbite.users.core.model.DriverProfile;
import com.quickbite.users.core.data.DriverProfileRepository;
import com.quickbite.users.core.data.UserRepository;
import com.quickbite.users.core.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverProfileRepository driverRepository;
    private final UserRepository userRepository;

    public DriverController(DriverProfileRepository driverRepository, UserRepository userRepository) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/profile")
    public ResponseEntity<DriverProfile> registerDriver(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DriverRegisterRequest request) {

        User user = userRepository.findByPhoneNumber(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        DriverProfile profile = driverRepository.findByUserId(user.getId())
                .orElse(new DriverProfile());

        profile.setUserId(user.getId());
        profile.setVehicleType(request.getVehicleType());
        profile.setVehicleNumber(request.getVehicleNumber());
        profile.setLicenseNumber(request.getLicenseNumber());
        profile.setVerified(false);

        DriverProfile saved = driverRepository.save(profile);
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/status")
    public ResponseEntity<DriverProfile> toggleOnlineStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody StatusRequest request) {

        User user = userRepository.findByPhoneNumber(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        DriverProfile profile = driverRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Driver profile not found"));

        profile.setOnline(request.isOnline());
        DriverProfile updated = driverRepository.save(profile);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/location")
    public ResponseEntity<Void> updateLocation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody LocationRequest request) {

        User user = userRepository.findByPhoneNumber(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        DriverProfile profile = driverRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Driver profile not found"));

        profile.setCurrentLatitude(request.getLatitude());
        profile.setCurrentLongitude(request.getLongitude());
        driverRepository.save(profile);

        return ResponseEntity.ok().build();
    }

    public static class DriverRegisterRequest {
        @NotBlank
        private String vehicleType;
        @NotBlank
        private String vehicleNumber;
        @NotBlank
        private String licenseNumber;

        public String getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
        }

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public void setVehicleNumber(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
        }

        public String getLicenseNumber() {
            return licenseNumber;
        }

        public void setLicenseNumber(String licenseNumber) {
            this.licenseNumber = licenseNumber;
        }
    }

    public static class StatusRequest {
        private boolean isOnline;

        public boolean isOnline() {
            return isOnline;
        }

        public void setOnline(boolean online) {
            isOnline = online;
        }
    }

    public static class LocationRequest {
        private Double latitude;
        private Double longitude;

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
    }
}