package com.quickbite.users.api.controllers;

import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.users.core.features.addaddress.AddAddressCommand;
import com.quickbite.users.core.features.getaddresses.GetCustomerAddressesQuery;
import com.quickbite.users.core.model.CustomerAddress;
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

    private final Mediator mediator;

    public CustomerController(Mediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/addresses")
    public ResponseEntity<CustomerAddress> addAddress(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddressRequest request) {

        AddAddressCommand command = new AddAddressCommand(
                userDetails.getUsername(),
                request.getAddressLine(),
                request.getLandmark(),
                request.getCity(),
                request.getPincode(),
                request.getLatitude(),
                request.getLongitude(),
                request.getTag(),
                request.isDefault());

        CustomerAddress saved = mediator.send(command);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<CustomerAddress>> getAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        List<CustomerAddress> addresses = mediator.send(new GetCustomerAddressesQuery(userDetails.getUsername()));
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