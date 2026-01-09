package com.chgvcode.y.users.auth.service;

import org.springframework.security.core.userdetails.UserDetails;

import com.chgvcode.y.users.model.UserEntity;

public interface IJwtService {
    public String generateToken(UserEntity user);

    public boolean isTokenValid(String token, UserDetails userDetails);

    public String extractUsername(String token);
}
