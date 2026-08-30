package com.quickbite.users.api.controllers;

import com.quickbite.buildingblocks.mediator.abstractions.Mediator;
import com.quickbite.users.core.features.loginuser.LoginUserCommand;
import com.quickbite.users.core.features.registeruser.RegisterUserCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final Mediator mediator;

    public AuthController(Mediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterUserCommand command) {
        mediator.send(command);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@RequestBody @Valid LoginUserCommand command) {
        Map<String, String> result = mediator.send(command);
        return ResponseEntity.ok(result);
    }
}