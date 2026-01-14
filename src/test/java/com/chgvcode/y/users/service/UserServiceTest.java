package com.chgvcode.y.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.chgvcode.y.users.auth.service.JwtService;
import com.chgvcode.y.users.dto.RegisterUserResponse;
import com.chgvcode.y.users.dto.UpdateUserRequest;
import com.chgvcode.y.users.dto.UserResponse;
import com.chgvcode.y.users.exception.ResourceNotFoundException;
import com.chgvcode.y.users.mapper.UserMapper;
import com.chgvcode.y.users.messaging.UserMessageProducer;
import com.chgvcode.y.users.model.UserDetailEntity;
import com.chgvcode.y.users.model.UserEntity;
import com.chgvcode.y.users.repository.UserDetailRepository;
import com.chgvcode.y.users.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetailRepository userDetailRepository;

    @Mock
    private UserMessageProducer userMessageProducer;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    public static final UUID USER_UUID = UUID.fromString("ff591668-59f4-4fd1-88e5-5d38ccbac94a");
    public static final String USER_USERNAME = "test";
    public static final String USER_PASSWORD = "testpass";
    public static final String USER_FIRST_NAME = "testing";
    public static final String USER_LAST_NAME = "tester";
    public static final Instant USER_CREATED_AT = Instant.parse("2026-01-14T12:00:00Z");

    public UserEntity USER_ENTITY_PREPARED = UserEntity.builder()
            .id(1L)
            .uuid(USER_UUID)
            .username(USER_USERNAME)
            .password(USER_PASSWORD)
            .createdAt(USER_CREATED_AT)
            .build();

    public UserResponse USER_RESPONSE_PREPARED = new UserResponse(
            USER_ENTITY_PREPARED.getUuid(),
            USER_ENTITY_PREPARED.getUsername(),
            USER_ENTITY_PREPARED.getCreatedAt());

    public UserDetailEntity USER_DETAIL_ENTITY_PREPARED = UserDetailEntity.builder()
        .firstName(USER_FIRST_NAME)
        .lastName(USER_LAST_NAME)
        .build();

    public RegisterUserResponse REGISTER_USER_RESPONSE_PREPARED = new RegisterUserResponse(
        USER_ENTITY_PREPARED.getUuid(),
        USER_ENTITY_PREPARED.getUsername(),
        USER_DETAIL_ENTITY_PREPARED.getFirstName(),
        USER_DETAIL_ENTITY_PREPARED.getLastName(),
        USER_ENTITY_PREPARED.getCreatedAt()
    );

    @Test
    void givenExistingUsername_whenGetUserByUsername_thenReturnsUserResponse() {
        when(userRepository.findByUsername(USER_USERNAME)).thenReturn(Optional.of(USER_ENTITY_PREPARED));
        
        when(userMapper.entityToUserResponse(USER_ENTITY_PREPARED)).thenReturn(USER_RESPONSE_PREPARED);

        UserResponse result = userService.getUserByUsername(USER_USERNAME);

        assertEquals(USER_UUID, result.uuid());
        assertEquals(USER_USERNAME, result.username());
        assertEquals(USER_ENTITY_PREPARED.getCreatedAt(), result.createdAt());
        verify(userRepository).findByUsername(USER_USERNAME);
    }

    @Test
    void givenNonExistingUsername_whenGetUserByUsername_thenThrowsResourceNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByUsername("unknown"));
        verify(userRepository).findByUsername("unknown");
    }

    @Test
    void givenUsernames_whenGetUserListByUsernames_thenReturnsMappedResponses() {
        List<String> usernames = List.of(USER_USERNAME, "another");
        UserEntity anotherUser = UserEntity.builder()
                .uuid(UUID.randomUUID())
                .username("another")
                .createdAt(Instant.now())
                .build();
        when(userRepository.findByUsernameIn(usernames)).thenReturn(List.of(USER_ENTITY_PREPARED, anotherUser));

        UserResponse anotherUserResponse = new UserResponse(
            anotherUser.getUuid(),
            anotherUser.getUsername(),
            anotherUser.getCreatedAt()
        );
        when(userMapper.entityToUserResponse(USER_ENTITY_PREPARED)).thenReturn(USER_RESPONSE_PREPARED);
        when(userMapper.entityToUserResponse(anotherUser)).thenReturn(anotherUserResponse);

        List<UserResponse> result = userService.getUserListByUsernames(usernames);

        assertEquals(2, result.size());
        assertEquals(USER_USERNAME, result.get(0).username());
        assertEquals("another", result.get(1).username());
        verify(userRepository).findByUsernameIn(usernames);
    }

    @Test
    void givenExistingUuid_whenGetUserByUuid_thenReturnsUserResponse() {
        when(userRepository.findByUuid(USER_UUID)).thenReturn(Optional.of(USER_ENTITY_PREPARED));

        when(userMapper.entityToUserResponse(USER_ENTITY_PREPARED)).thenReturn(USER_RESPONSE_PREPARED);

        UserResponse result = userService.getUserByUuid(USER_UUID);

        assertEquals(USER_UUID, result.uuid());
        assertEquals(USER_USERNAME, result.username());
        verify(userRepository).findByUuid(USER_UUID);
    }

    @Test
    void givenNonExistingUuid_whenGetUserByUuid_thenThrowsResourceNotFound() {
        UUID unknownUuid = UUID.randomUUID();
        when(userRepository.findByUuid(unknownUuid)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByUuid(unknownUuid));
        verify(userRepository).findByUuid(unknownUuid);
    }

    @Test
    void givenUuids_whenGetUsersByUuids_thenReturnsMappedResponses() {
        UUID anotherUuid = UUID.randomUUID();
        UserEntity anotherUser = UserEntity.builder()
                .uuid(anotherUuid)
                .username("another")
                .createdAt(Instant.now())
                .build();
        List<UUID> uuids = List.of(USER_UUID, anotherUuid);
        when(userRepository.findByUuidIn(uuids)).thenReturn(List.of(USER_ENTITY_PREPARED, anotherUser));

        when(userMapper.entityToUserResponse(USER_ENTITY_PREPARED)).thenReturn(USER_RESPONSE_PREPARED);

        List<UserResponse> result = userService.getUsersByUuids(uuids);

        assertEquals(2, result.size());
        assertEquals(USER_UUID, result.get(0).uuid());
        verify(userRepository).findByUuidIn(uuids);
    }

    @Test
    void givenPageable_whenGetUsers_thenReturnsPagedResponses() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> mockPage = new PageImpl<>(List.of(USER_ENTITY_PREPARED), pageable, 1);
        when(userRepository.findAll(pageable)).thenReturn(mockPage);

        when(userMapper.entityToUserResponse(USER_ENTITY_PREPARED)).thenReturn(USER_RESPONSE_PREPARED);

        Page<UserResponse> result = userService.getUsers(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(USER_USERNAME, result.getContent().get(0).username());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void givenValidUserData_whenCreateUser_thenSavesAndSendsMessage() {
        when(userRepository.save(any(UserEntity.class))).thenReturn(USER_ENTITY_PREPARED);

        when(userDetailRepository.save(any(UserDetailEntity.class))).thenReturn(USER_DETAIL_ENTITY_PREPARED);

        when(userMapper.entityToUserResponse(USER_ENTITY_PREPARED)).thenReturn(USER_RESPONSE_PREPARED);
        when(userMapper.entityToRegisterUserResponse(USER_ENTITY_PREPARED, USER_DETAIL_ENTITY_PREPARED)).thenReturn(REGISTER_USER_RESPONSE_PREPARED);

        RegisterUserResponse result = userService.createUser(USER_USERNAME, USER_PASSWORD, USER_FIRST_NAME, USER_LAST_NAME);

        assertEquals(USER_UUID, result.uuid());
        assertEquals(USER_USERNAME, result.username());
        assertEquals(USER_FIRST_NAME, result.firstName());
        assertEquals(USER_LAST_NAME, result.lastName());
        assertEquals(USER_CREATED_AT, result.createdAt());
        verify(userRepository).save(any(UserEntity.class));
        verify(userDetailRepository).save(any(UserDetailEntity.class));
        verify(userMessageProducer).sendUserCreated(any(UserResponse.class));
    }

    @Test
    void givenValidUpdateRequest_whenUpdateUser_thenUpdatesFields() {
        UpdateUserRequest request = new UpdateUserRequest("newtest", "New", "Test");
        UserDetailEntity detail = new UserDetailEntity(USER_ENTITY_PREPARED, "Old", "Names");

        when(userRepository.findByUsername(USER_USERNAME)).thenReturn(Optional.of(USER_ENTITY_PREPARED));
        when(userDetailRepository.findByUser(USER_ENTITY_PREPARED)).thenReturn(detail);

        userService.updateUser(USER_USERNAME, request);

        assertEquals("newtest", USER_ENTITY_PREPARED.getUsername());
        assertEquals("New", detail.getFirstName());
        assertEquals("Test", detail.getLastName());
        verify(userDetailRepository).save(detail);
        verify(userRepository).save(USER_ENTITY_PREPARED);
    }

    @Test
    void givenNonExistingUsername_whenUpdateUser_thenThrowsResourceNotFound() {
        UpdateUserRequest request = new UpdateUserRequest("new", "name", "last");
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser("unknown", request));
        verify(userRepository).findByUsername("unknown");
    }

    @Test
    void givenExistingUsername_whenDeleteUser_thenDeletesAndSendsMessage() {
        when(userRepository.findByUsername(USER_USERNAME)).thenReturn(Optional.of(USER_ENTITY_PREPARED));

        userService.deleteUser(USER_USERNAME);

        verify(userRepository).deleteByUsername(USER_USERNAME);
        verify(userMessageProducer).sendUserDeleted(USER_ENTITY_PREPARED);
    }

    @Test
    void givenNonExistingUsername_whenDeleteUser_thenThrowsResourceNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteUser("unknown"));
        verify(userRepository, never()).deleteByUsername(anyString());
    }

    @Test
    void givenExistingUuid_whenGenerateToken_thenReturnsToken() {
        when(userRepository.findByUuid(USER_UUID)).thenReturn(Optional.of(USER_ENTITY_PREPARED));
        when(jwtService.generateToken(USER_ENTITY_PREPARED)).thenReturn("jwt-token-123");

        String result = userService.generateToken(USER_UUID);

        assertEquals("jwt-token-123", result);
        verify(jwtService).generateToken(USER_ENTITY_PREPARED);
    }

    @Test
    void givenNonExistingUuid_whenGenerateToken_thenThrowsResourceNotFound() {
        UUID unknownUuid = UUID.randomUUID();
        when(userRepository.findByUuid(unknownUuid)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.generateToken(unknownUuid));
        verifyNoInteractions(jwtService);
    }
}
