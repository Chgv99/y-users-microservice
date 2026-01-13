package com.chgvcode.y.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chgvcode.y.users.model.UserDetailEntity;
import com.chgvcode.y.users.model.UserEntity;



public interface UserDetailRepository extends JpaRepository<UserDetailEntity, Long> {
    public UserDetailEntity findByUser(UserEntity user);
}
