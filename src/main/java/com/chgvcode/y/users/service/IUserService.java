package com.chgvcode.y.users.service;

import java.util.List;

import com.chgvcode.y.users.model.UserEntity;

public interface IUserService {
    public List<UserEntity> getUsers();
    public UserEntity createUser(String username, String password);
}
