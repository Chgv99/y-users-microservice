package com.chgvcode.y.users.auth.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.ResponseCookie;

public class RefreshCookieFactory {

    public static ResponseCookie create(String refreshToken, Instant expiresAt) {
        long maxAgeSeconds = Duration.between(Instant.now(), expiresAt).getSeconds();
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) // false only local
                .sameSite("Lax") // or "Strict"
                .path("/auth/refresh")
                .maxAge(maxAgeSeconds)
                .build();
    }
}
