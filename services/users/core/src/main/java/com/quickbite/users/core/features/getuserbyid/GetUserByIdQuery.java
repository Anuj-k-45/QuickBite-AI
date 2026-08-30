package com.quickbite.users.core.features.getuserbyid;

import com.quickbite.buildingblocks.mediator.abstractions.IQuery;
import com.quickbite.users.core.model.User;
import java.util.UUID;

public record GetUserByIdQuery(UUID id) implements IQuery<User> {
}