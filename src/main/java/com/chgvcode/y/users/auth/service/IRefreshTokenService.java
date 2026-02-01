package com.chgvcode.y.users.auth.service;

import java.util.UUID;

import com.chgvcode.y.users.auth.model.RefreshToken;

public interface IRefreshTokenService {
    public RefreshToken createRefreshToken(UUID userUuid);
    public RefreshToken findByHash(String hashedToken);
    public void revokeByHash(String hashedToken);
    public void revokeAllByUserUuid(UUID userUuid);
    public String sha256(String token);
}
