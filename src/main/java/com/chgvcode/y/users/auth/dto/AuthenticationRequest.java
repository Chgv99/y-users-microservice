package com.chgvcode.y.users.auth.dto;

public record AuthenticationRequest(
    String username,
    String password
) {}
