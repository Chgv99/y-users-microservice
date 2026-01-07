package com.chgvcode.y.users.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chgvcode.y.users.dto.UserRequest;
import com.chgvcode.y.users.dto.UserResponse;
import com.chgvcode.y.users.messaging.UserMessageProducer;
import com.chgvcode.y.users.model.UserEntity;
import com.chgvcode.y.users.service.IUserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final IUserService userService;

    private final UserMessageProducer userMessageProducer;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(
            @RequestParam(required = false) List<UUID> uuids) {
        
        List<UserEntity> users;
        if (uuids != null && !uuids.isEmpty()) {
            users = userService.getUsersByUuids(uuids);
        } else {
            users = userService.getUsers();
        }
        
        List<UserResponse> userResponses = users.stream()
            .map(u -> new UserResponse(u.getId(), u.getUuid(), u.getUsername(), u.getCreatedAt()))
            .toList();
            
        return ResponseEntity.ok(userResponses);
    }

    // @PostMapping
    // public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
    //     UserEntity user = userService.createUser(request.username(), request.password());
    //     UserResponse userResponse = new UserResponse(user.getId(), user.getUuid(), user.getUsername(), user.getCreatedAt());
    //     userMessageProducer.sendMessage(user);
    //     return new ResponseEntity<>(userResponse, HttpStatus.OK);
    // }
}
