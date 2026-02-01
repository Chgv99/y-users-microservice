package com.chgvcode.y.users.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.chgvcode.y.users.dto.UpdateUserRequest;
import com.chgvcode.y.users.exception.ResourceAlreadyExistsException;
import com.chgvcode.y.users.exception.ResourceNotFoundException;
import com.chgvcode.y.users.mapper.UserMapper;
import com.chgvcode.y.users.messaging.UserMessageProducer;
import com.chgvcode.y.users.model.User;
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
    private final UserMapper userMapper;

    public User getUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(username));
        return userMapper.toModel(userEntity);
    }

    public List<User> getUserListByUsernames(List<String> usernames) {
        List<UserEntity> userEntities = userRepository.findByUsernameIn(usernames);
        return userEntities.stream()
                .map(userEntity -> userMapper.toModel(userEntity))
                .toList();
    }

    public User getUserByUuid(UUID uuid) {
        UserEntity userEntity = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(uuid.toString()));
        return userMapper.toModel(userEntity);
    }

    public List<User> getUsersByUuids(List<UUID> uuids) {
        List<UserEntity> userEntities = userRepository.findByUuidIn(uuids);
        return userEntities.stream()
                .map(userEntity -> userMapper.toModel(userEntity))
                .toList();
    }

    public Page<User> getUsers(Pageable pageable) {
        Page<UserEntity> page = userRepository.findAll(pageable);
        List<User> users = page.stream()
                .map(userEntity -> userMapper.toModel(userEntity))
                .toList();
        return new PageImpl<>(users, pageable, page.getTotalElements());
    }

    @Transactional
    public User createUser(String username, String password, String firstName, String lastName) {
        UserEntity user = new UserEntity(username, password);
        try {
            UserEntity savedUserEntity = userRepository.save(user);
            User savedUser = userMapper.toModel(savedUserEntity);

            userMessageProducer.sendUserCreated(savedUser);

            UserDetailEntity userDetail = new UserDetailEntity(savedUserEntity, firstName, lastName);
            UserDetailEntity savedUserDetail = userDetailRepository.save(userDetail);

            return savedUser;
        } catch (DataIntegrityViolationException dive) {
            throw new ResourceAlreadyExistsException(user.getUsername());
        }
    }

    @Transactional
    public void updateUser(String username, UpdateUserRequest request) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(username));

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
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(username));
        userRepository.deleteByUsername(username);
        userMessageProducer.sendUserDeleted(userEntity);
    }
}
