package com.quickbite.users.core.features.getaddresses;

import com.quickbite.buildingblocks.mediator.abstractions.IQueryHandler;
import com.quickbite.users.core.data.CustomerAddressRepository;
import com.quickbite.users.core.data.UserRepository;
import com.quickbite.users.core.model.CustomerAddress;
import com.quickbite.users.core.model.User;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class GetCustomerAddressesQueryHandler
        implements IQueryHandler<GetCustomerAddressesQuery, List<CustomerAddress>> {

    private final CustomerAddressRepository addressRepository;
    private final UserRepository userRepository;

    public GetCustomerAddressesQueryHandler(CustomerAddressRepository addressRepository,
            UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CustomerAddress> handle(GetCustomerAddressesQuery query) {
        User user = userRepository.findByPhoneNumber(query.phoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return addressRepository.findByCustomerId(user.getId());
    }
}