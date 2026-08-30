package com.quickbite.users.core.features.getaddresses;

import com.quickbite.buildingblocks.mediator.abstractions.IQuery;
import com.quickbite.users.core.model.CustomerAddress;
import java.util.List;

public record GetCustomerAddressesQuery(String phoneNumber) implements IQuery<List<CustomerAddress>> {
}