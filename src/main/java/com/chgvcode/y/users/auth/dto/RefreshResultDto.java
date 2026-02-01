package com.chgvcode.y.users.auth.dto;

import com.chgvcode.y.users.auth.model.RefreshToken;

public record RefreshResultDto(
    AccessTokenDto accessToken,
    RefreshToken refreshToken
) {}
