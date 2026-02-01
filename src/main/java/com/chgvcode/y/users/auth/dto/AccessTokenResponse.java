package com.chgvcode.y.users.auth.dto;

public record AccessTokenResponse(
    String token,
    Long expiresIn
) {}
