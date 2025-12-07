package com.chgvcode.y.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chgvcode.y.users.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
}
