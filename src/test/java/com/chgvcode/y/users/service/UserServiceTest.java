package com.chgvcode.y.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.chgvcode.y.users.auth.service.JwtService;
import com.chgvcode.y.users.config.Role;
import com.chgvcode.y.users.dto.RegisterUserResponse;
import com.chgvcode.y.users.dto.UpdateUserRequest;
import com.chgvcode.y.users.dto.UserResponse;
import com.chgvcode.y.users.exception.ResourceAlreadyExistsException;
import com.chgvcode.y.users.exception.ResourceNotFoundException;
import com.chgvcode.y.users.mapper.UserMapper;
import com.chgvcode.y.users.messaging.UserMessageProducer;
import com.chgvcode.y.users.model.User;
import com.chgvcode.y.users.model.UserDetail;
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

    public static final Long USER_ID = 1L;
    public static final UUID USER_UUID = UUID.fromString("ff591668-59f4-4fd1-88e5-5d38ccbac94a");
    public static final String USER_USERNAME = "test";
    public static final String USER_PASSWORD = "testpass";
    public static final Role USER_ROLE = Role.USER;
    public static final String USER_FIRST_NAME = "testing";
    public static final String USER_LAST_NAME = "tester";
    public static final Instant USER_CREATED_AT = Instant.parse("2026-01-14T12:00:00Z");

    private static final Long ANOTHER_USER_ID = 2L;
    private static final UUID ANOTHER_USER_UUID = UUID.fromString("40cbd43c-668f-4c49-a48a-37064efd8c58");
    private static final String ANOTHER_USER_USERNAME = USER_USERNAME + "2";
    private static final String ANOTHER_USER_PASSWORD = "sarcófago";
    private static final Role ANOTHER_USER_ROLE = Role.USER;
    private static final String ANOTHER_USER_FIRST_NAME = "Another";
    private static final String ANOTHER_USER_LAST_NAME = "User";
    private static final Instant ANOTHER_USER_CREATED_AT = Instant.now();

    public static final String UNKNOWN_USER_USERNAME = "unknown";

    public UserEntity USER_ENTITY_PREPARED = UserEntity.builder()
            .id(USER_ID)
            .uuid(USER_UUID)
            .username(USER_USERNAME)
            .password(USER_PASSWORD)
            .createdAt(USER_CREATED_AT)
            .build();

    public UserDetail USER_DETAIL_PREPARED = new UserDetail(
            USER_ID,
            USER_FIRST_NAME,
            USER_LAST_NAME);

    public User USER_PREPARED = new User(
            USER_ID,
            USER_UUID,
            USER_USERNAME,
            USER_PASSWORD,
            USER_ROLE,
            USER_DETAIL_PREPARED,
            USER_CREATED_AT);

    UserDetail ANOTHER_USER_DETAIL_PREPARED = new UserDetail(
            ANOTHER_USER_ID,
            ANOTHER_USER_FIRST_NAME,
            ANOTHER_USER_LAST_NAME);

    UserEntity ANOTHER_USER_ENTITY_PREPARED = UserEntity.builder()
            .uuid(ANOTHER_USER_UUID)
            .username(ANOTHER_USER_USERNAME)
            .createdAt(ANOTHER_USER_CREATED_AT)
            .build();

    User ANOTHER_USER_PREPARED = new User(
            2L,
            ANOTHER_USER_UUID,
            ANOTHER_USER_USERNAME,
            ANOTHER_USER_PASSWORD,
            ANOTHER_USER_ROLE,
            ANOTHER_USER_DETAIL_PREPARED,
            ANOTHER_USER_CREATED_AT);

    // public UserResponse USER_RESPONSE_PREPARED = new UserResponse(
    // USER_ENTITY_PREPARED.getUuid(),
    // USER_ENTITY_PREPARED.getUsername(),
    // USER_ENTITY_PREPARED.getRole(),
    // USER_ENTITY_PREPARED
    // USER_ENTITY_PREPARED.getCreatedAt());

    public UserDetailEntity USER_DETAIL_ENTITY_PREPARED = UserDetailEntity.builder()
            .firstName(USER_FIRST_NAME)
            .lastName(USER_LAST_NAME)
            .build();

    public RegisterUserResponse REGISTER_USER_RESPONSE_PREPARED = new RegisterUserResponse(
            USER_ENTITY_PREPARED.getUuid(),
            USER_ENTITY_PREPARED.getUsername(),
            USER_DETAIL_ENTITY_PREPARED.getFirstName(),
            USER_DETAIL_ENTITY_PREPARED.getLastName(),
            USER_ENTITY_PREPARED.getRole(),
            USER_ENTITY_PREPARED.getCreatedAt());

    @Test
    void givenExistingUsername_whenGetUserByUsername_thenReturnsUser() {
        when(userRepository.findByUsername(USER_USERNAME)).thenReturn(Optional.of(USER_ENTITY_PREPARED));
        when(userMapper.toModel(USER_ENTITY_PREPARED)).thenReturn(USER_PREPARED);

        User result = userService.getUserByUsername(USER_USERNAME);

        assertEquals(USER_UUID, result.uuid());
        assertEquals(USER_USERNAME, result.username());
        assertEquals(USER_ENTITY_PREPARED.getCreatedAt(), result.createdAt());
        verify(userRepository).findByUsername(USER_USERNAME);
    }

    @Test
    void givenNonExistingUsername_whenGetUserByUsername_thenThrowsResourceNotFound() {
        when(userRepository.findByUsername(UNKNOWN_USER_USERNAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByUsername(UNKNOWN_USER_USERNAME));
        verify(userRepository).findByUsername(UNKNOWN_USER_USERNAME);
    }

    @Test
    void givenUsernames_whenGetUserListByUsernames_thenReturnsMappedResponses() {

        List<String> usernames = List.of(USER_USERNAME, ANOTHER_USER_USERNAME);

        when(userRepository.findByUsernameIn(usernames))
                .thenReturn(List.of(USER_ENTITY_PREPARED, ANOTHER_USER_ENTITY_PREPARED));
        when(userMapper.toModel(USER_ENTITY_PREPARED)).thenReturn(USER_PREPARED);
        when(userMapper.toModel(ANOTHER_USER_ENTITY_PREPARED)).thenReturn(ANOTHER_USER_PREPARED);

        List<User> result = userService.getUserListByUsernames(usernames);

        assertEquals(2, result.size());
        assertEquals(USER_USERNAME, result.get(0).username());
        assertEquals(ANOTHER_USER_USERNAME, result.get(1).username());
        verify(userRepository).findByUsernameIn(usernames);
    }

    @Test
    void givenExistingUuid_whenGetUserByUuid_thenReturnsUserResponse() {
        when(userRepository.findByUuid(USER_UUID)).thenReturn(Optional.of(USER_ENTITY_PREPARED));
        when(userMapper.toModel(USER_ENTITY_PREPARED)).thenReturn(USER_PREPARED);

        User result = userService.getUserByUuid(USER_UUID);

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
        List<UUID> uuids = List.of(USER_UUID, ANOTHER_USER_UUID);
        when(userRepository.findByUuidIn(uuids))
                .thenReturn(List.of(USER_ENTITY_PREPARED, ANOTHER_USER_ENTITY_PREPARED));

        when(userMapper.toModel(USER_ENTITY_PREPARED)).thenReturn(USER_PREPARED);
        when(userMapper.toModel(ANOTHER_USER_ENTITY_PREPARED)).thenReturn(ANOTHER_USER_PREPARED);

        List<User> result = userService.getUsersByUuids(uuids);

        assertEquals(2, result.size());
        assertEquals(USER_UUID, result.get(0).uuid());
        assertEquals(ANOTHER_USER_UUID, result.get(1).uuid());
        verify(userRepository).findByUuidIn(uuids);
    }

    @Test
    void givenPageable_whenGetUsers_thenReturnsPagedResponses() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> mockPage = new PageImpl<>(List.of(USER_ENTITY_PREPARED), pageable, 1);
        when(userRepository.findAll(pageable)).thenReturn(mockPage);
        when(userMapper.toModel(USER_ENTITY_PREPARED)).thenReturn(USER_PREPARED);

        Page<User> result = userService.getUsers(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(USER_USERNAME, result.getContent().get(0).username());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void givenValidUserData_whenCreateUser_thenSavesAndSendsMessage() {
        when(userRepository.save(any(UserEntity.class))).thenReturn(USER_ENTITY_PREPARED);
        when(userDetailRepository.save(any(UserDetailEntity.class))).thenReturn(USER_DETAIL_ENTITY_PREPARED);
        when(userMapper.toModel(USER_ENTITY_PREPARED)).thenReturn(USER_PREPARED);

        User result = userService.createUser(USER_USERNAME, USER_PASSWORD, USER_FIRST_NAME, USER_LAST_NAME);
        assertEquals(USER_UUID, result.uuid());
        assertEquals(USER_USERNAME, result.username());
        assertEquals(USER_FIRST_NAME, result.detail().getFirstName());
        assertEquals(USER_LAST_NAME, result.detail().getLastName());
        assertEquals(USER_CREATED_AT, result.createdAt());
        verify(userRepository).save(any(UserEntity.class));
        verify(userDetailRepository).save(any(UserDetailEntity.class));
        verify(userMessageProducer).sendUserCreated(any(User.class));
    }

    @Test
    void givenExistingUserData_whenCreateUser_thenThrowsResourceAlreadyExistsException() {
        when(userRepository.save(any(UserEntity.class))).thenThrow(DataIntegrityViolationException.class);
        assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.createUser(USER_USERNAME, USER_PASSWORD, USER_FIRST_NAME, USER_LAST_NAME));
    }

    // TODO: REVIEW
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
        UpdateUserRequest request = new UpdateUserRequest("newUsername", "newFirst", "newLast");
        when(userRepository.findByUsername(UNKNOWN_USER_USERNAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(UNKNOWN_USER_USERNAME, request));
        verify(userRepository).findByUsername(UNKNOWN_USER_USERNAME);
    }

    @Test
    void givenEmptyUpdateRequestUsername_whenUpdateUser_thenUsernameDoesNotChange() {
        final String NEW_FIRST_NAME = "newFirst";
        final String NEW_LAST_NAME = "newLast";
        UpdateUserRequest request = new UpdateUserRequest("", NEW_FIRST_NAME, NEW_LAST_NAME);

        when(userRepository.findByUsername(USER_USERNAME)).thenReturn(Optional.of(USER_ENTITY_PREPARED));
        when(userDetailRepository.findByUser(USER_ENTITY_PREPARED)).thenReturn(USER_DETAIL_ENTITY_PREPARED);

        userService.updateUser(USER_USERNAME, request);

        // Username didn't change
        assertEquals(USER_USERNAME, USER_ENTITY_PREPARED.getUsername());        
        
        // Firstname and lastname did change
        assertEquals(NEW_FIRST_NAME, USER_DETAIL_ENTITY_PREPARED.getFirstName());
        assertEquals(NEW_LAST_NAME, USER_DETAIL_ENTITY_PREPARED.getLastName());

        verify(userDetailRepository).save(USER_DETAIL_ENTITY_PREPARED);
        verify(userRepository).save(USER_ENTITY_PREPARED);
    }

    @Test
    void givenEmptyUpdateRequestFirstname_whenUpdateUser_thenFirstnameDoesNotChange() {
        final String NEW_USERNAME = "newUsername";
        final String NEW_LAST_NAME = "newLast";
        UpdateUserRequest request = new UpdateUserRequest(NEW_USERNAME, "", NEW_LAST_NAME);

        when(userRepository.findByUsername(USER_USERNAME)).thenReturn(Optional.of(USER_ENTITY_PREPARED));
        when(userDetailRepository.findByUser(USER_ENTITY_PREPARED)).thenReturn(USER_DETAIL_ENTITY_PREPARED);

        userService.updateUser(USER_USERNAME, request);

        // Firstname didn't change
        assertEquals(USER_FIRST_NAME, USER_DETAIL_ENTITY_PREPARED.getFirstName());
        
        // Username and lastname did change
        assertEquals(NEW_USERNAME, USER_ENTITY_PREPARED.getUsername());        
        assertEquals(NEW_LAST_NAME, USER_DETAIL_ENTITY_PREPARED.getLastName());

        // Changed details must be saved
        verify(userDetailRepository).save(USER_DETAIL_ENTITY_PREPARED);
        
        verify(userRepository).save(USER_ENTITY_PREPARED);
    }

    @Test
    void givenEmptyUpdateRequestLastname_whenUpdateUser_thenLastnameDoesNotChange() {
        final String NEW_USERNAME = "newUsername";
        final String NEW_FIRST_NAME = "newFirst";
        UpdateUserRequest request = new UpdateUserRequest(NEW_USERNAME, NEW_FIRST_NAME, "");

        when(userRepository.findByUsername(USER_USERNAME)).thenReturn(Optional.of(USER_ENTITY_PREPARED));
        when(userDetailRepository.findByUser(USER_ENTITY_PREPARED)).thenReturn(USER_DETAIL_ENTITY_PREPARED);

        userService.updateUser(USER_USERNAME, request);

        // Lastname didn't change
        assertEquals(USER_LAST_NAME, USER_DETAIL_ENTITY_PREPARED.getLastName());
        
        // Username and firstname did change
        assertEquals(NEW_USERNAME, USER_ENTITY_PREPARED.getUsername());        
        assertEquals(NEW_FIRST_NAME, USER_DETAIL_ENTITY_PREPARED.getFirstName());

        // Changed details must be saved
        verify(userDetailRepository).save(USER_DETAIL_ENTITY_PREPARED);

        verify(userRepository).save(USER_ENTITY_PREPARED);
    }

    @Test
    void givenEmptyUpdateRequestNames_whenUpdateUser_thenNamesDoNotChange() {
        final String NEW_USERNAME = "newUsername";
        UpdateUserRequest request = new UpdateUserRequest(NEW_USERNAME, "", "");

        when(userRepository.findByUsername(USER_USERNAME)).thenReturn(Optional.of(USER_ENTITY_PREPARED));

        userService.updateUser(USER_USERNAME, request);

        // Firstname and lastname didn't change
        assertEquals(USER_FIRST_NAME, USER_DETAIL_ENTITY_PREPARED.getFirstName());
        assertEquals(USER_LAST_NAME, USER_DETAIL_ENTITY_PREPARED.getLastName());
        
        // Username did change
        assertEquals(NEW_USERNAME, USER_ENTITY_PREPARED.getUsername());        

        // Changed details must not be saved, since they did not change
        verify(userDetailRepository, never()).save(USER_DETAIL_ENTITY_PREPARED);

        verify(userRepository).save(USER_ENTITY_PREPARED);
    }

    @Test
    void givenExistingUsername_whenDeleteUser_thenDeletesAndSendsMessage() {
        when(userRepository.findByUsername(USER_USERNAME)).thenReturn(Optional.of(USER_ENTITY_PREPARED));
        when(userMapper.toModel(USER_ENTITY_PREPARED)).thenReturn(USER_PREPARED);
        
        userService.deleteUser(USER_USERNAME);
        verify(userRepository).deleteByUsername(USER_USERNAME);
        verify(userMessageProducer).sendUserDeleted(USER_PREPARED);
    }

    @Test
    void givenNonExistingUsername_whenDeleteUser_thenThrowsResourceNotFound() {
        when(userRepository.findByUsername(UNKNOWN_USER_USERNAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteUser(UNKNOWN_USER_USERNAME));
        verify(userRepository, never()).deleteByUsername(anyString());
    }
}
