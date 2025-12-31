package com.chgvcode.y.users.service;

import java.util.List;
import java.util.UUID;

import com.chgvcode.y.users.model.UserEntity;

public interface IUserService {
    public List<UserEntity> getUsers();
    public List<UserEntity> getUsersByUuids(List<UUID> uuids);
    public UserEntity createUser(String username, String password);
}
