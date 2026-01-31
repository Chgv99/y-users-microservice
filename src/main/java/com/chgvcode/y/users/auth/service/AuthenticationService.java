package com.chgvcode.y.users.auth.service;

import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chgvcode.y.users.auth.dto.AuthenticationRequest;
import com.chgvcode.y.users.auth.dto.AuthenticationResponse;
import com.chgvcode.y.users.auth.dto.RefreshResult;
import com.chgvcode.y.users.auth.dto.RefreshTokenResponse;
import com.chgvcode.y.users.auth.dto.RegisterRequest;
import com.chgvcode.y.users.auth.dto.RegisterResponse;
import com.chgvcode.y.users.auth.dto.TokenResponse;
import com.chgvcode.y.users.dto.RegisterUserResponse;
import com.chgvcode.y.users.dto.UserResponse;
import com.chgvcode.y.users.exception.UnauthorizedException;
import com.chgvcode.y.users.mapper.AuthenticationMapper;
import com.chgvcode.y.users.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

    private final UserService userService;

    private final IJwtService jwtService;

    private final IRefreshTokenService refreshTokenService;

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

    @Transactional
    public RefreshResult refresh(String refreshToken) {
        String incomingHash = refreshTokenService.sha256(refreshToken);
        RefreshTokenResponse storedRefreshToken = refreshTokenService.findByHash(incomingHash);

        if (storedRefreshToken.revoked()) {
            throw new UnauthorizedException();
        }

        if (storedRefreshToken.expiresAt().isBefore(Instant.now())) {
            refreshTokenService.revokeByHash(storedRefreshToken.token());
            throw new UnauthorizedException();
        }

        refreshTokenService.revokeByHash(storedRefreshToken.token());
        UserResponse userResponse = userService.getUserByUuid(storedRefreshToken.userUuid());
        RefreshTokenResponse newRefreshToken = refreshTokenService.createRefreshToken(userResponse.uuid());
        TokenResponse accessResponse = jwtService.generateToken(userResponse.uuid().toString(), userResponse.username(), userResponse.role(), userResponse.createdAt());
        TokenResponse refreshResponse = new TokenResponse(newRefreshToken.token(), "Refresh", newRefreshToken.expiresAt().toEpochMilli());
        return new RefreshResult(accessResponse, refreshResponse);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserResponse userResponse = userService.getUserByUsername(request.username());
        TokenResponse tokenResponse = jwtService.generateToken(userResponse.uuid().toString(), userResponse.username(), userResponse.role(),
                userResponse.createdAt());

        return new AuthenticationResponse(userResponse, tokenResponse);
    }
}
