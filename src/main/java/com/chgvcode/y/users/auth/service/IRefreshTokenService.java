package com.chgvcode.y.users.auth.service;

import java.util.UUID;

import com.chgvcode.y.users.auth.dto.RefreshTokenResponse;

public interface IRefreshTokenService {
    public RefreshTokenResponse createRefreshToken(UUID userUuid);
    public RefreshTokenResponse findByHash(String hashedToken);
    public void revokeByHash(String hashedToken);
    public void revokeAllByUserUuid(UUID userUuid);
    public String sha256(String token);
}
