package com.chgvcode.y.users.auth.dto;

public record RegisterRequest(
    String username,
    String password,
    String firstName,
    String lastName
) {}
