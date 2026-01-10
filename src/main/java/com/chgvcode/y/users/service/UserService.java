package com.chgvcode.y.users.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.chgvcode.y.users.dto.UserResponse;
import com.chgvcode.y.users.messaging.UserMessageProducer;
import com.chgvcode.y.users.model.UserEntity;
import com.chgvcode.y.users.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    
    private final UserRepository userRepository;
    private final UserMessageProducer userMessageProducer;

    public UserResponse getUserByUsername(String username) {
        UserEntity userEntity = getUserEntityByUsername(username);
        return new UserResponse(userEntity.getId(), userEntity.getUuid(), userEntity.getUsername(), userEntity.getCreatedAt());
    }

    public List<UserResponse> getUserListByUsernames(List<String> usernames) {
        List<UserEntity> userEntities = userRepository.findByUsernameIn(usernames);
        return userEntities.stream()
                .map(userEntity -> new UserResponse(userEntity.getId(), userEntity.getUuid(), userEntity.getUsername(),
                        userEntity.getCreatedAt()))
                .toList();
    }

    public UserEntity getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    public UserResponse getUserByUuid(UUID uuid) {
        UserEntity userEntity = userRepository.findByUuid(uuid).orElseThrow();
        return new UserResponse(userEntity.getId(), userEntity.getUuid(), userEntity.getUsername(), userEntity.getCreatedAt());
    }

    public List<UserResponse> getUsersByUuids(List<UUID> uuids) {
        List<UserEntity> userEntities = userRepository.findByUuidIn(uuids);
        return userEntities.stream()
                .map(userEntity -> new UserResponse(userEntity.getId(), userEntity.getUuid(), userEntity.getUsername(),
                        userEntity.getCreatedAt()))
                .toList();
    }

    public Page<UserResponse> getUsers(Pageable pageable) {
        Page<UserEntity> page = userRepository.findAll(pageable);
        List<UserResponse> userResponses = page.stream()
                .map(userEntity -> new UserResponse(userEntity.getId(), userEntity.getUuid(), userEntity.getUsername(),
                        userEntity.getCreatedAt()))
                .toList();
        return new PageImpl<>(userResponses, pageable, page.getTotalElements());
    }

    public UserResponse createUser(String username, String password) {
        UserEntity userEntity = createUserEntity(username, password);
        return new UserResponse(userEntity.getId(), userEntity.getUuid(),
                userEntity.getUsername(), userEntity.getCreatedAt());
    }

    public UserEntity createUserEntity(String username, String password) {
        UserEntity user = new UserEntity(username, password);
        userMessageProducer.sendUserCreated(user);
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        userRepository.deleteByUsername(username);
        userMessageProducer.sendUserDeleted(userEntity);
    }
}
