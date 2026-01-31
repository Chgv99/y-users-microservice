package com.chgvcode.y.users.auth.dto;

public record RefreshResult(
    TokenResponse accessToken,
    TokenResponse refreshToken
) {}
