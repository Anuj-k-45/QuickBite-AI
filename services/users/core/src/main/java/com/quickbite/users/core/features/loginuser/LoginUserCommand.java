package com.quickbite.users.core.features.loginuser;

import com.quickbite.buildingblocks.mediator.abstractions.ICommand;
import java.util.Map;

public record LoginUserCommand(
        String phoneNumber,
        String password) implements ICommand<Map<String, String>> {
}