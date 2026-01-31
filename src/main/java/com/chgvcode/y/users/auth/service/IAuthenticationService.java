package com.chgvcode.y.users.auth.service;

import com.chgvcode.y.users.auth.dto.AuthenticationRequest;
import com.chgvcode.y.users.auth.dto.AuthenticationResponse;
import com.chgvcode.y.users.auth.dto.RefreshResult;
import com.chgvcode.y.users.auth.dto.RegisterRequest;
import com.chgvcode.y.users.auth.dto.RegisterResponse;

public interface IAuthenticationService {
    public RegisterResponse register(RegisterRequest request);
    public RefreshResult refresh(String refreshToken);
    public AuthenticationResponse authenticate(AuthenticationRequest request);
}
