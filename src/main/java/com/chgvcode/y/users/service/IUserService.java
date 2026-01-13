package com.chgvcode.y.users.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.chgvcode.y.users.dto.RegisterUserResponse;
import com.chgvcode.y.users.dto.UserResponse;

public interface IUserService {
    public UserResponse getUserByUsername(String username);
    public List<UserResponse> getUserListByUsernames(List<String> usernames);
    public UserResponse getUserByUuid(UUID uuid);
    public List<UserResponse> getUsersByUuids(List<UUID> uuids);
    public Page<UserResponse> getUsers(Pageable pageable);
    public RegisterUserResponse createUser(String username, String password, String firstName, String lastName);
    public void deleteUser(String username);
    public String generateToken(UUID uuid);
}
