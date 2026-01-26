package com.chgvcode.y.users.auth.controller;

import org.apache.hc.core5.http.NotImplementedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chgvcode.y.users.auth.dto.AuthenticationRequest;
import com.chgvcode.y.users.auth.dto.TokenResponse;
import com.chgvcode.y.users.auth.model.RefreshTokenEntity;
import com.chgvcode.y.users.auth.dto.RegisterRequest;
import com.chgvcode.y.users.auth.dto.RegisterResponse;
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
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
        RegisterResponse registerResponse = authenticationService.register(request);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(registerResponse.uuid());
        ResponseCookie cookie = RefreshCookieFactory.create(refreshToken.getToken(), refreshToken.getExpiresAt());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(registerResponse);
    }

    // TODO
    // @PostMapping("/refresh")
    // public ResponseEntity<?> refresh() {
    //     throw new NotImplementedException();
    //     return ResponseEntity.ok();
    // }
    
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
