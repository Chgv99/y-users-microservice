package com.chgvcode.y.users.auth.controller;

import java.time.Instant;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chgvcode.y.users.auth.dto.AuthenticationRequest;
import com.chgvcode.y.users.auth.dto.AuthenticationResponse;
import com.chgvcode.y.users.auth.dto.RefreshResult;
import com.chgvcode.y.users.auth.dto.RefreshTokenResponse;
import com.chgvcode.y.users.auth.dto.RegisterRequest;
import com.chgvcode.y.users.auth.dto.RegisterResponse;
import com.chgvcode.y.users.auth.dto.TokenResponse;
import com.chgvcode.y.users.auth.service.IAuthenticationService;
import com.chgvcode.y.users.auth.service.IRefreshTokenService;
import com.chgvcode.y.users.auth.service.RefreshCookieFactory;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final IAuthenticationService authenticationService;

    private final IRefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        RegisterResponse registerResponse = authenticationService.register(request);

        RefreshTokenResponse refreshToken = refreshTokenService.createRefreshToken(registerResponse.uuid());
        ResponseCookie cookie = RefreshCookieFactory.create(refreshToken.token(), refreshToken.expiresAt());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(registerResponse);
    }

    @GetMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @CookieValue("refresh_token") String refreshToken,
            HttpServletResponse response) {
        RefreshResult result = authenticationService.refresh(refreshToken);
        ResponseCookie cookie = RefreshCookieFactory.create(result.refreshToken().token(),
                Instant.ofEpochMilli(result.refreshToken().expiresIn()));
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(result.accessToken());
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@Valid @RequestBody AuthenticationRequest request, HttpServletResponse response) {
        AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);

        RefreshTokenResponse refreshToken = refreshTokenService.createRefreshToken(authenticationResponse.user().uuid());
        ResponseCookie cookie = RefreshCookieFactory.create(refreshToken.token(), refreshToken.expiresAt());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(authenticationResponse.token());
    }
}
