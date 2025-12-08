package com.chgvcode.y.users.auth.service;

import com.chgvcode.y.users.auth.dto.AuthenticationRequest;
import com.chgvcode.y.users.auth.dto.AuthenticationResponse;
import com.chgvcode.y.users.auth.dto.RegisterRequest;

public interface IAuthenticationService {
    public AuthenticationResponse register(RegisterRequest request);
    public AuthenticationResponse authenticate(AuthenticationRequest request);
}
