package com.chgvcode.y.users.auth.dto;

public record AccessTokenDto(
    String token,
    Long expiresIn
) {}
