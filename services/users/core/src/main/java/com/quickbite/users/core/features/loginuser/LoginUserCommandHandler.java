package com.quickbite.users.core.features.loginuser;

import com.quickbite.buildingblocks.mediator.abstractions.ICommandHandler;
import com.quickbite.buildingblocks.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class LoginUserCommandHandler implements ICommandHandler<LoginUserCommand, Map<String, String>> {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public LoginUserCommandHandler(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Map<String, String> handle(LoginUserCommand command) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(command.phoneNumber(), command.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return Map.of("token", jwt, "tokenType", "Bearer");
    }
}