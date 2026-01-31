package com.chgvcode.y.users.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chgvcode.y.users.auth.model.RefreshTokenEntity;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    public Optional<RefreshTokenEntity> findByToken(String token);
    public List<RefreshTokenEntity> findAllByUserUuid(UUID userUuid);
}
