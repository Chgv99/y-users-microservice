package com.chgvcode.y.users.auth.dto;

import java.time.Instant;
import java.util.UUID;

public record RefreshTokenResponse(
    UUID userUuid,
    String token,
    Instant expiresAt,
    Boolean revoked
) {}
