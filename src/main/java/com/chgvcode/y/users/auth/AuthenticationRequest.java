package com.chgvcode.y.users.auth;

public record AuthenticationRequest(
    String username,
    String password
) {}
