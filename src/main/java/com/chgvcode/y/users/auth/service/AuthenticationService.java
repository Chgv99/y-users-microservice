package com.chgvcode.y.users.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chgvcode.y.users.auth.dto.AuthenticationRequest;
import com.chgvcode.y.users.auth.dto.AuthenticationResponse;
import com.chgvcode.y.users.auth.dto.RegisterRequest;
import com.chgvcode.y.users.config.Role;
import com.chgvcode.y.users.model.UserEntity;
import com.chgvcode.y.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final IJwtService jwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = UserEntity.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        String jwt = jwtService.generateToken(user);
        return new AuthenticationResponse(jwt);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        var user = userRepository.findByUsername(request.username()).orElseThrow();

        String jwt = jwtService.generateToken(user);
        return new AuthenticationResponse(jwt);
    }
}
