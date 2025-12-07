package com.chgvcode.y.users.dto;

import java.time.Instant;

public record UserResponse(
    Long id,
    String username,
    String password,
    Instant createdAt
) {}
