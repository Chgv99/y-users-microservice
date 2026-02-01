package com.chgvcode.y.users.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chgvcode.y.users.auth.dto.AccessTokenResponse;
import com.chgvcode.y.users.auth.dto.AuthenticationDto;
import com.chgvcode.y.users.auth.dto.AuthenticationRequest;
import com.chgvcode.y.users.auth.dto.RefreshResultDto;
import com.chgvcode.y.users.auth.dto.RegisterDto;
import com.chgvcode.y.users.auth.dto.RegisterRequest;
import com.chgvcode.y.users.auth.mapper.AccessTokenMapper;
import com.chgvcode.y.users.auth.service.IAuthenticationService;
import com.chgvcode.y.users.auth.service.RefreshCookieFactory;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final IAuthenticationService authenticationService;

    private final AccessTokenMapper accessTokenMapper;

    @PostMapping("/register")
    public ResponseEntity<AccessTokenResponse> register(@Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        RegisterDto registerDto = authenticationService.register(request);

        ResponseCookie cookie = RefreshCookieFactory.create(registerDto.refreshToken().token(),
                registerDto.refreshToken().expiresAt());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(accessTokenMapper.toResponse(registerDto.accessToken()));
    }

    @GetMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> refresh(
            @CookieValue("refresh_token") String refreshToken,
            HttpServletResponse response) {
        RefreshResultDto result = authenticationService.refresh(refreshToken);
        ResponseCookie cookie = RefreshCookieFactory.create(result.refreshToken().token(),
                result.refreshToken().expiresAt());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(accessTokenMapper.toResponse(result.accessToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> authenticate(@Valid @RequestBody AuthenticationRequest request,
            HttpServletResponse response) {
        AuthenticationDto authenticationDto = authenticationService.authenticate(request);

        ResponseCookie cookie = RefreshCookieFactory.create(authenticationDto.refreshToken().token(),
                authenticationDto.refreshToken().expiresAt());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(accessTokenMapper.toResponse(authenticationDto.accessToken()));
    }
}
