package com.chgvcode.y.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chgvcode.y.users.model.UserDetailEntity;


public interface UserDetailRepository extends JpaRepository<UserDetailEntity, Long> {
    
}
