package com.quickbite.users.core.features.customeraddress;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;
import com.quickbite.users.core.model.CustomerAddress;

public record AddAddressCommand(
        String phoneNumber,
        String addressLine,
        String landmark,
        String city,
        String pincode,
        Double latitude,
        Double longitude,
        String tag,
        boolean isDefault) implements ICommand<CustomerAddress> {
}