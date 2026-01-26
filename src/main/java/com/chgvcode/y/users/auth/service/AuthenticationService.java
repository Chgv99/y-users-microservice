package com.chgvcode.y.users.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chgvcode.y.users.auth.dto.AuthenticationRequest;
import com.chgvcode.y.users.auth.dto.RegisterRequest;
import com.chgvcode.y.users.auth.dto.RegisterResponse;
import com.chgvcode.y.users.auth.dto.TokenResponse;
import com.chgvcode.y.users.dto.RegisterUserResponse;
import com.chgvcode.y.users.dto.UserResponse;
import com.chgvcode.y.users.mapper.AuthenticationMapper;
import com.chgvcode.y.users.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

    private final UserService userService;

    private final IJwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final AuthenticationMapper authenticationMapper;

    public RegisterResponse register(RegisterRequest request) {
        RegisterUserResponse userResponse = userService.createUser(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName());

        TokenResponse accessToken = jwtService.generateToken(userResponse.uuid().toString(), userResponse.username(),
                userResponse.role(), userResponse.createdAt());
        return authenticationMapper.toRegisterResponse(userResponse, accessToken);
    }

    public TokenResponse authenticate(AuthenticationRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserResponse userResponse = userService.getUserByUsername(request.username());

        return jwtService.generateToken(userResponse.uuid().toString(), userResponse.username(), userResponse.role(),
                userResponse.createdAt());
    }
}
