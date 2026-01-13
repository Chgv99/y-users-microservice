package com.chgvcode.y.users.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.chgvcode.y.users.auth.service.JwtService;
import com.chgvcode.y.users.dto.RegisterUserResponse;
import com.chgvcode.y.users.dto.UpdateUserRequest;
import com.chgvcode.y.users.dto.UserResponse;
import com.chgvcode.y.users.messaging.UserMessageProducer;
import com.chgvcode.y.users.model.UserDetailEntity;
import com.chgvcode.y.users.model.UserEntity;
import com.chgvcode.y.users.repository.UserDetailRepository;
import com.chgvcode.y.users.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UserDetailRepository userDetailRepository;
    private final UserMessageProducer userMessageProducer;
    private final JwtService jwtService;

    public UserResponse getUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        return new UserResponse(userEntity.getUuid(), userEntity.getUsername(),
                userEntity.getCreatedAt());
    }

    public List<UserResponse> getUserListByUsernames(List<String> usernames) {
        List<UserEntity> userEntities = userRepository.findByUsernameIn(usernames);
        return userEntities.stream()
                .map(userEntity -> new UserResponse(userEntity.getUuid(), userEntity.getUsername(),
                        userEntity.getCreatedAt()))
                .toList();
    }

    public UserResponse getUserByUuid(UUID uuid) {
        UserEntity userEntity = userRepository.findByUuid(uuid).orElseThrow();
        return new UserResponse(userEntity.getUuid(), userEntity.getUsername(),
                userEntity.getCreatedAt());
    }

    public List<UserResponse> getUsersByUuids(List<UUID> uuids) {
        List<UserEntity> userEntities = userRepository.findByUuidIn(uuids);
        return userEntities.stream()
                .map(userEntity -> new UserResponse(userEntity.getUuid(), userEntity.getUsername(),
                        userEntity.getCreatedAt()))
                .toList();
    }

    public Page<UserResponse> getUsers(Pageable pageable) {
        Page<UserEntity> page = userRepository.findAll(pageable);
        List<UserResponse> userResponses = page.stream()
                .map(userEntity -> new UserResponse(userEntity.getUuid(), userEntity.getUsername(),
                        userEntity.getCreatedAt()))
                .toList();
        return new PageImpl<>(userResponses, pageable, page.getTotalElements());
    }

    public RegisterUserResponse createUser(String username, String password, String firstName, String lastName) {
        UserEntity user = new UserEntity(username, password);
        UserEntity savedUser = userRepository.save(user);
        userMessageProducer.sendUserCreated(new UserResponse(savedUser.getUuid(),
                savedUser.getUsername(), savedUser.getCreatedAt()));

        UserDetailEntity userDetail = new UserDetailEntity(savedUser, firstName, lastName);
        UserDetailEntity savedUserDetail = userDetailRepository.save(userDetail);
        return new RegisterUserResponse(savedUser.getUuid(), savedUser.getUsername(), savedUserDetail.getFirstName(), savedUserDetail.getLastName(), savedUser.getCreatedAt());
    }

    @Transactional
    public void updateUser(String username, UpdateUserRequest request) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();

        if (!request.username().isEmpty()) {
            userEntity.setUsername(request.username());
        }

        if (!request.firstName().isEmpty() && !request.lastName().isEmpty()) {
            UserDetailEntity userDetailEntity = userDetailRepository.findByUser(userEntity);

            if (!request.firstName().isEmpty()) {
                userDetailEntity.setFirstName(request.firstName());
            }

            if (!request.lastName().isEmpty()) {
                userDetailEntity.setLastName(request.lastName());
            }

            userDetailRepository.save(userDetailEntity);
        }
        
        userRepository.save(userEntity);
    }

    @Transactional
    public void deleteUser(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        userRepository.deleteByUsername(username);
        userMessageProducer.sendUserDeleted(userEntity);
    }

    public String generateToken(UUID uuid) {
        UserEntity userEntity = userRepository.findByUuid(uuid).orElseThrow();
        return jwtService.generateToken(userEntity);
    }
}
