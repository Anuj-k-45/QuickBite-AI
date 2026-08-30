package com.quickbite.users.core.features.getuserbyid;

import com.quickbite.buildingblocks.mediator.abstractions.IQueryHandler;
import com.quickbite.users.core.data.UserRepository;
import com.quickbite.users.core.model.User;
import com.quickbite.users.core.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class GetUserByIdQueryHandler implements IQueryHandler<GetUserByIdQuery, User> {
    private final UserRepository userRepository;

    public GetUserByIdQueryHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User handle(GetUserByIdQuery query) {
        return userRepository.findById(query.id())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + query.id()));
    }
}