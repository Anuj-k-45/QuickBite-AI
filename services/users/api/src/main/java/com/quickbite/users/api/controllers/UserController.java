package com.quickbite.users.api.controllers;

import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.users.core.features.getuserbyid.GetUserByIdQuery;
import com.quickbite.users.core.features.getuserbyphone.GetUserByPhoneQuery;
import com.quickbite.users.core.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final Mediator mediator;

    public UserController(Mediator mediator) {
        this.mediator = mediator;
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUserProfile(Authentication authentication) {
        User user = mediator.send(new GetUserByPhoneQuery(authentication.getName()));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        User user = mediator.send(new GetUserByIdQuery(id));
        return ResponseEntity.ok(user);
    }
}