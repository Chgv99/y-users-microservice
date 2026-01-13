package com.chgvcode.y.users.dto;

public record UserRequest (
    String username,
    String password,
    String firstName,
    String lastName
){}
