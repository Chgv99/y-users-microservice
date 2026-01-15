package com.chgvcode.y.users.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Size(min = 4, max = 20) String username,
    @NotBlank @Size(min = 4, max = 255) String password,
    @NotBlank @Size(max = 255) String firstName,
    @NotBlank @Size(max = 255) String lastName
) {}
