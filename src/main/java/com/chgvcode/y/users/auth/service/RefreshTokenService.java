package com.chgvcode.y.users.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chgvcode.y.users.auth.mapper.RefreshTokenMapper;
import com.chgvcode.y.users.auth.model.RefreshToken;
import com.chgvcode.y.users.auth.model.RefreshTokenEntity;
import com.chgvcode.y.users.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements IRefreshTokenService {

    private final RefreshTokenMapper refreshTokenMapper;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    @Value("${application.security.refresh_token.expiration}")
    private int EXPIRATION_DAYS;

    public RefreshToken createRefreshToken(UUID userUuid) {
        String refreshToken = generate();
        String hashedToken = sha256(refreshToken);
        RefreshTokenEntity savedHashedRefreshToken = refreshTokenRepository
                .save(new RefreshTokenEntity(userUuid, hashedToken,
                        Instant.now().plus(EXPIRATION_DAYS, ChronoUnit.DAYS), false));

        return new RefreshToken(savedHashedRefreshToken.getUserUuid(), refreshToken,
                savedHashedRefreshToken.getExpiresAt(), savedHashedRefreshToken.getRevoked());
    }

    private String generate() {
        byte[] randomBytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public RefreshToken findByHash(String hashedToken) {
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(hashedToken).orElseThrow();
        return refreshTokenMapper.toModel(refreshTokenEntity);
    }

    public void revokeByHash(String hashedToken) {
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByToken(hashedToken).orElseThrow();
        refreshTokenEntity.setRevoked(true);
        refreshTokenRepository.save(refreshTokenEntity);
    }

    @Transactional
    public void revokeAllByUserUuid(UUID userUuid) {
        List<RefreshTokenEntity> refreshTokenList = refreshTokenRepository.findAllByUserUuid(userUuid);
        refreshTokenList.forEach((refreshToken) -> refreshToken.setRevoked(true));
    }

    public String sha256(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
