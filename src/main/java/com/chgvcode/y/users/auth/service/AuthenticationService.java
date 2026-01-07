package com.chgvcode.y.users.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chgvcode.y.users.auth.dto.AuthenticationRequest;
import com.chgvcode.y.users.auth.dto.AuthenticationResponse;
import com.chgvcode.y.users.auth.dto.RegisterRequest;
import com.chgvcode.y.users.messaging.UserMessageProducer;
import com.chgvcode.y.users.model.UserEntity;
import com.chgvcode.y.users.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

    private final UserService userService;

    private final UserMessageProducer userMessageProducer;

    private final PasswordEncoder passwordEncoder;

    private final IJwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        UserEntity userEntity = userService.createUser(request.username(), passwordEncoder.encode(request.password()));

        String jwt = jwtService.generateToken(userEntity);
        userMessageProducer.sendMessage(userEntity);
        return new AuthenticationResponse(jwt);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        var user = userService.getByUsername(request.username());

        String jwt = jwtService.generateToken(user);
        return new AuthenticationResponse(jwt);
    }
}
