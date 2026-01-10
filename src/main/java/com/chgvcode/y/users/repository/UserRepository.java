package com.chgvcode.y.users.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chgvcode.y.users.model.UserEntity;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    public Optional<UserEntity> findByUuid(UUID uuid);
    public Optional<UserEntity> findByUsername(String username);
    public List<UserEntity> findByUsernameIn(List<String> usernames);
    public List<UserEntity> findByUuidIn(List<UUID> uuids);
    public void deleteByUsername(String username);
}
