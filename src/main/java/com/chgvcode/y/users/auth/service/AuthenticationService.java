package com.chgvcode.y.users.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chgvcode.y.users.auth.dto.AuthenticationRequest;
import com.chgvcode.y.users.auth.dto.AuthenticationResponse;
import com.chgvcode.y.users.auth.dto.RegisterRequest;
import com.chgvcode.y.users.auth.dto.RegisterResponse;
import com.chgvcode.y.users.dto.RegisterUserResponse;
import com.chgvcode.y.users.dto.UserResponse;
import com.chgvcode.y.users.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public RegisterResponse register(RegisterRequest request) {
        RegisterUserResponse userResponse = userService.createUser(
            request.username(), 
            passwordEncoder.encode(request.password()),
            request.firstName(),
            request.lastName()
        );

        String jwt = userService.generateToken(userResponse.uuid());
        return new RegisterResponse(userResponse.uuid(), userResponse.username(), userResponse.firstName(), userResponse.lastName(), jwt);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserResponse userResponse = userService.getUserByUsername(request.username());

        String jwt = userService.generateToken(userResponse.uuid());
        return new AuthenticationResponse(jwt);
    }
}
