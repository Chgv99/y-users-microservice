package com.chgvcode.y.users.auth.dto;

import com.chgvcode.y.users.dto.UserResponse;

public record AuthenticationResponse(
    UserResponse user,
    TokenDto token
) {}
