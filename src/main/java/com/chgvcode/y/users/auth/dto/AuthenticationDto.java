package com.chgvcode.y.users.auth.dto;

import java.util.UUID;

import com.chgvcode.y.users.auth.model.RefreshToken;

public record AuthenticationDto(
    UUID uuid,
    String username,
    String firstName,
    String lastName,
    AccessTokenDto accessToken,
    RefreshToken refreshToken
) {}
