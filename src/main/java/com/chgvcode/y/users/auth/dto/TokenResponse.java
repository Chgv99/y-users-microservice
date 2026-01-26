package com.chgvcode.y.users.auth.dto;

public record TokenResponse(
    String token,
    String type,
    Long expiresIn
) {}
