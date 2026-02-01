package com.chgvcode.y.users.auth.service;

import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.chgvcode.y.users.auth.dto.AccessTokenDto;
import com.chgvcode.y.users.auth.dto.AuthenticationDto;
import com.chgvcode.y.users.auth.dto.AuthenticationRequest;
import com.chgvcode.y.users.auth.dto.RefreshResultDto;
import com.chgvcode.y.users.auth.dto.RegisterDto;
import com.chgvcode.y.users.auth.dto.RegisterRequest;
import com.chgvcode.y.users.auth.model.RefreshToken;
import com.chgvcode.y.users.exception.UnauthorizedException;
import com.chgvcode.y.users.mapper.AuthenticationMapper;
import com.chgvcode.y.users.mapper.RegisterMapper;
import com.chgvcode.y.users.model.User;
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

    private final RegisterMapper registerMapper;

    private final AuthenticationMapper authenticationMapper;

    public RegisterDto register(RegisterRequest request) {
        User user = userService.createUser(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName());

        AccessTokenDto accessToken = jwtService.generateToken(user.uuid().toString(), user.username(),
                user.role(), user.createdAt());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.uuid());
        return registerMapper.toDto(user, accessToken, refreshToken);
    }

    @Transactional
    public RefreshResultDto refresh(String refreshToken) {
        String incomingHash = refreshTokenService.sha256(refreshToken);
        RefreshToken storedRefreshToken = refreshTokenService.findByHash(incomingHash);

        if (storedRefreshToken.revoked()) {
            throw new UnauthorizedException();
        }

        if (storedRefreshToken.expiresAt().isBefore(Instant.now())) {
            refreshTokenService.revokeByHash(storedRefreshToken.token());
            throw new UnauthorizedException();
        }

        refreshTokenService.revokeByHash(storedRefreshToken.token());
        User user = userService.getUserByUuid(storedRefreshToken.userUuid());
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.uuid());
        AccessTokenDto accessResponse = jwtService.generateToken(user.uuid().toString(),
                user.username(), user.role(), user.createdAt());
        return new RefreshResultDto(accessResponse, newRefreshToken);

    }

    public AuthenticationDto authenticate(AuthenticationRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = userService.getUserByUsername(request.username());
        AccessTokenDto accessToken = jwtService.generateToken(user.uuid().toString(), user.username(),
                user.role(),
                user.createdAt());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.uuid());
        return authenticationMapper.toDto(user, accessToken, refreshToken);
    }
}
