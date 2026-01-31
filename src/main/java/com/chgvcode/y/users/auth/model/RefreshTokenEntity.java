package com.chgvcode.y.users.auth.model;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "refresh_token")
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_uuid", nullable = false)
    private UUID userUuid;

    private String token;

    @Column(name = "expires_at")
    private Instant expiresAt;

    private Boolean revoked;

    public RefreshTokenEntity(UUID userUuid, String token, Instant expiresAt, Boolean revoked) {
        this.userUuid = userUuid;
        this.token = token;
        this.expiresAt = expiresAt;
        this.revoked = revoked;
    }
}
