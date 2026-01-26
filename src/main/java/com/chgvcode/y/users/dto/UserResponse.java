package com.chgvcode.y.users.dto;

import java.time.Instant;
import java.util.UUID;

import com.chgvcode.y.users.config.Role;

public record UserResponse(
    UUID uuid,
    String username,
    Role role,
    Instant createdAt
) {}
