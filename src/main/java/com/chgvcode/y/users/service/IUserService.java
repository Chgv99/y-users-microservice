package com.chgvcode.y.users.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.chgvcode.y.users.dto.UpdateUserRequest;
import com.chgvcode.y.users.model.User;

public interface IUserService {
    public User getUserByUsername(String username);
    public List<User> getUserListByUsernames(List<String> usernames);
    public User getUserByUuid(UUID uuid);
    public List<User> getUsersByUuids(List<UUID> uuids);
    public Page<User> getUsers(Pageable pageable);
    public User createUser(String username, String password, String firstName, String lastName);
    public void updateUser(String username, UpdateUserRequest request);
    public void deleteUser(String username);
}
