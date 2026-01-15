package com.chgvcode.y.users.auth.dto;

public record TokenResponse(
    String accessToken,
    String tokenType,
    Long expiresIn
) {}
