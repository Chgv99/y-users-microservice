package com.chgvcode.y.users.auth.model;

import java.time.Instant;
import java.util.UUID;

public record RefreshToken(
    UUID userUuid,
    String token,
    Instant expiresAt,
    Boolean revoked
) {}
