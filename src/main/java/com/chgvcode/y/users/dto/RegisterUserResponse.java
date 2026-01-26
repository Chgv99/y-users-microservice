package com.chgvcode.y.users.dto;

import java.time.Instant;
import java.util.UUID;

import com.chgvcode.y.users.config.Role;

public record RegisterUserResponse(
    UUID uuid,
    String username,
    String firstName,
    String lastName,
    Role role,
    Instant createdAt
) {}
