package com.chgvcode.y.users.auth.service;

import java.time.Instant;

import org.springframework.security.core.userdetails.UserDetails;

import com.chgvcode.y.users.auth.dto.TokenResponse;
import com.chgvcode.y.users.config.Role;

public interface IJwtService {
    public TokenResponse generateToken(String uuid, String username, Role role, Instant createdAt);

    public boolean isTokenValid(String token, UserDetails userDetails);

    public String extractUsername(String token);

    public long getExpirationSeconds(String token);
}
