package com.chgvcode.y.users.auth.service;

import java.util.UUID;

import com.chgvcode.y.users.auth.model.RefreshTokenEntity;

public interface IRefreshTokenService {
    public RefreshTokenEntity createRefreshToken(UUID userUuid);
}
