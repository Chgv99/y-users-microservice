package com.chgvcode.y.users.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chgvcode.y.users.auth.model.RefreshTokenEntity;
import com.chgvcode.y.users.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements IRefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    @Value("${application.security.refresh_token.expiration}")
    private int EXPIRATION_DAYS;

    public RefreshTokenEntity createRefreshToken(UUID userUuid) {
        String refreshToken = generate();
        String hashedToken = sha256(refreshToken);
        RefreshTokenEntity savedHashedRefreshToken = refreshTokenRepository
                .save(new RefreshTokenEntity(userUuid, hashedToken, Instant.now().plus(EXPIRATION_DAYS, ChronoUnit.DAYS), null));
        return savedHashedRefreshToken;
    }

    private String generate() {
        byte[] randomBytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    private String sha256(String token) {
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
