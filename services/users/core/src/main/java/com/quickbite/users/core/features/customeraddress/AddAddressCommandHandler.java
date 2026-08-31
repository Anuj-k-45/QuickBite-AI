package com.quickbite.users.core.features.customeraddress;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.users.core.data.CustomerAddressRepository;
import com.quickbite.users.core.data.UserRepository;
import com.quickbite.users.core.model.CustomerAddress;
import com.quickbite.users.core.model.User;
import org.springframework.stereotype.Component;

@Component
public class AddAddressCommandHandler implements ICommandHandler<AddAddressCommand, CustomerAddress> {

    private final CustomerAddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddAddressCommandHandler(CustomerAddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CustomerAddress handle(AddAddressCommand command) {
        User user = userRepository.findByPhoneNumber(command.phoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomerAddress address = new CustomerAddress();
        address.setCustomerId(user.getId());
        address.setAddressLine(command.addressLine());
        address.setLandmark(command.landmark());
        address.setCity(command.city());
        address.setPincode(command.pincode());
        address.setLatitude(command.latitude());
        address.setLongitude(command.longitude());
        address.setTag(command.tag() != null ? command.tag() : "HOME");
        address.setDefault(command.isDefault());

        return addressRepository.save(address);
    }
}