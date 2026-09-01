package com.quickbite.users.core.features.registeruser;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.users.core.data.UserRepository;
import com.quickbite.users.core.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class RegisterUserCommandHandler implements ICommandHandler<RegisterUserCommand, Void> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserCommandHandler(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Void handle(RegisterUserCommand command) {
        if (userRepository.existsByPhoneNumber(command.phoneNumber())) {
            throw new RuntimeException("Phone number is already registered");
        }

        // Handle role formatting safely
        String inputRole = command.role();
        String roleToAssign = "ROLE_CUSTOMER"; // default

        if (inputRole != null && !inputRole.isBlank()) {
            String upper = inputRole.trim().toUpperCase();
            roleToAssign = upper.startsWith("ROLE_") ? upper : "ROLE_" + upper;
        }

        User user = new User();
        user.setPhoneNumber(command.phoneNumber());
        user.setEmail(command.email());
        user.setPasswordHash(passwordEncoder.encode(command.password()));
        user.setFirstName(command.firstName());
        user.setLastName(command.lastName());
        user.setRoles(Set.of(roleToAssign));

        userRepository.save(user);
        return null;
    }
}