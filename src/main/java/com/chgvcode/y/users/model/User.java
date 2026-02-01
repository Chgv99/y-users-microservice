package com.chgvcode.y.users.model;

import java.time.Instant;
import java.util.UUID;

import com.chgvcode.y.users.config.Role;

public record User(
    Long id,
    UUID uuid,
    String username,
    String password,
    Role role,
    UserDetail detail,
    Instant createdAt
) {}
