package com.chgvcode.y.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chgvcode.y.users.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    public Optional<UserEntity> findByUsername(String username);
}
