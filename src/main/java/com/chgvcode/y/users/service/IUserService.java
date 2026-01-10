package com.chgvcode.y.users.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.chgvcode.y.users.dto.UserResponse;
import com.chgvcode.y.users.model.UserEntity;

public interface IUserService {
    public UserResponse getUserByUsername(String username);
    public List<UserResponse> getUserListByUsernames(List<String> usernames);
    public UserEntity getUserEntityByUsername(String username);
    public UserResponse getUserByUuid(UUID uuid);
    public List<UserResponse> getUsersByUuids(List<UUID> uuids);
    public Page<UserResponse> getUsers(Pageable pageable);
    public UserResponse createUser(String username, String password);
    public UserEntity createUserEntity(String username, String password);
    public void deleteUser(String username);
}
