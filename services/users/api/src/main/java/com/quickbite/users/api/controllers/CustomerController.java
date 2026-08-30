package com.quickbite.users.api.controllers;

import com.quickbite.users.core.model.CustomerAddress;
import com.quickbite.users.core.data.CustomerAddressRepository;
import com.quickbite.users.core.data.UserRepository;
import com.quickbite.users.core.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerAddressRepository addressRepository;
    private final UserRepository userRepository;

    public CustomerController(CustomerAddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/addresses")
    public ResponseEntity<CustomerAddress> addAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddressRequest request) {

        User user = userRepository.findByPhoneNumber(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomerAddress address = new CustomerAddress();
        address.setCustomerId(user.getId());
        address.setAddressLine(request.getAddressLine());
        address.setLandmark(request.getLandmark());
        address.setCity(request.getCity());
        address.setPincode(request.getPincode());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setTag(request.getTag() != null ? request.getTag() : "HOME");
        address.setDefault(request.isDefault());

        CustomerAddress saved = addressRepository.save(address);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<CustomerAddress>> getAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByPhoneNumber(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CustomerAddress> addresses = addressRepository.findByCustomerId(user.getId());
        return ResponseEntity.ok(addresses);
    }

    public static class AddressRequest {
        @NotBlank
        private String addressLine;
        private String landmark;
        @NotBlank
        private String city;
        @NotBlank
        private String pincode;
        private Double latitude;
        private Double longitude;
        private String tag;
        private boolean isDefault;

        // Getters and Setters
        public String getAddressLine() {
            return addressLine;
        }

        public void setAddressLine(String addressLine) {
            this.addressLine = addressLine;
        }

        public String getLandmark() {
            return landmark;
        }

        public void setLandmark(String landmark) {
            this.landmark = landmark;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

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

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public void setDefault(boolean aDefault) {
            isDefault = aDefault;
        }
    }
}