package com.quickbite.users.api.controllers;

import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.users.core.features.registerdriver.RegisterDriverCommand;
import com.quickbite.users.core.features.toggledriverstatus.ToggleDriverStatusCommand;
import com.quickbite.users.core.features.updatedriverlocation.UpdateDriverLocationCommand;
import com.quickbite.users.core.model.DriverProfile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final Mediator mediator;

    public DriverController(Mediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/profile")
    public ResponseEntity<DriverProfile> registerDriver(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DriverRegisterRequest request) {

        RegisterDriverCommand command = new RegisterDriverCommand(
                userDetails.getUsername(),
                request.getVehicleType(),
                request.getVehicleNumber(),
                request.getLicenseNumber());

        DriverProfile saved = mediator.send(command);
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/status")
    public ResponseEntity<DriverProfile> toggleOnlineStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody StatusRequest request) {

        DriverProfile updated = mediator
                .send(new ToggleDriverStatusCommand(userDetails.getUsername(), request.isOnline()));
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/location")
    public ResponseEntity<Void> updateLocation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody LocationRequest request) {

        mediator.send(new UpdateDriverLocationCommand(userDetails.getUsername(), request.getLatitude(),
                request.getLongitude()));
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