package com.chgvcode.y.users.dto;

public record UserRequest (
    String username,
    String password,
    String first_name,
    String last_name
){}
