package com.quickbite.users.core.features.getuserbyphone;

import com.quickbite.buildingblocks.mediator.abstractions.IQuery;
import com.quickbite.users.core.model.User;

public record GetUserByPhoneQuery(String phoneNumber) implements IQuery<User> {
}