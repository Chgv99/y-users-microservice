package com.chgvcode.y.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chgvcode.y.users.auth.model.RefreshTokenEntity;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    
}
