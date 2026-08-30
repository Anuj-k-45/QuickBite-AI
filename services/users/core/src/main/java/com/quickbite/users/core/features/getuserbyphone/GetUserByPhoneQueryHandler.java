package com.quickbite.users.core.features.getuserbyphone;

import com.quickbite.buildingblocks.mediator.abstractions.IQueryHandler;
import com.quickbite.users.core.data.UserRepository;
import com.quickbite.users.core.model.User;
import com.quickbite.users.core.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class GetUserByPhoneQueryHandler implements IQueryHandler<GetUserByPhoneQuery, User> {
    private final UserRepository userRepository;

    public GetUserByPhoneQueryHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User handle(GetUserByPhoneQuery query) {
        return userRepository.findByPhoneNumber(query.phoneNumber())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with phone: " + query.phoneNumber()));
    }
}