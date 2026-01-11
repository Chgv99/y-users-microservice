package com.chgvcode.y.users.auth.dto;

import java.util.UUID;

public record RegisterResponse(
    UUID uuid,
    String username,
    String firstName,
    String lastName,
    String jwt
) {}
