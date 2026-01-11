package com.chgvcode.y.users.dto;

import java.time.Instant;
import java.util.UUID;

public record RegisterUserResponse(
    UUID uuid,
    String username,
    String firstName,
    String lastName,
    Instant createdAt
) {}
