package com.chgvcode.y.users.auth.service;

import com.chgvcode.y.users.auth.dto.AuthenticationDto;
import com.chgvcode.y.users.auth.dto.AuthenticationRequest;
import com.chgvcode.y.users.auth.dto.RefreshResultDto;
import com.chgvcode.y.users.auth.dto.RegisterDto;
import com.chgvcode.y.users.auth.dto.RegisterRequest;

public interface IAuthenticationService {
    public RegisterDto register(RegisterRequest request);
    public RefreshResultDto refresh(String refreshToken);
    public AuthenticationDto authenticate(AuthenticationRequest request);
}
