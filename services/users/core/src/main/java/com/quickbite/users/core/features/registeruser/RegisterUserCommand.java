package com.quickbite.users.core.features.registeruser;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;

public record RegisterUserCommand(
                String phoneNumber,
                String email,
                String password,
                String firstName,
                String lastName,
                String role) implements ICommand<Void> {
}