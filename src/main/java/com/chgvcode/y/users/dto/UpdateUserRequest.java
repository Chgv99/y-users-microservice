package com.chgvcode.y.users.dto;

public record UpdateUserRequest (
    String username,
    String firstName,
    String lastName
){}
