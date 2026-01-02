package com.chgvcode.y.users.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.chgvcode.y.users.model.UserEntity;
import com.chgvcode.y.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;

    // Needs to map userentity into userresponse
    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }
    
    public List<UserEntity> getUsersByUuids(List<UUID> uuids) {
        return userRepository.findByUuidIn(uuids);
    }
    
    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public UserEntity createUser(String username, String password) {
        UserEntity user = new UserEntity(username, password);
        return userRepository.save(user);
    }
}
