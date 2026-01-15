package com.chgvcode.y.users.auth.service;

import com.chgvcode.y.users.auth.dto.AuthenticationRequest;
import com.chgvcode.y.users.auth.dto.TokenResponse;
import com.chgvcode.y.users.auth.dto.RegisterRequest;
import com.chgvcode.y.users.auth.dto.RegisterResponse;

public interface IAuthenticationService {
    public RegisterResponse register(RegisterRequest request);
    public TokenResponse authenticate(AuthenticationRequest request);
}
