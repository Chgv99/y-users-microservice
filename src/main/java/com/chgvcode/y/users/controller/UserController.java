package com.chgvcode.y.users.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chgvcode.y.users.dto.UserRequest;
import com.chgvcode.y.users.dto.UserResponse;
import com.chgvcode.y.users.model.UserEntity;
import com.chgvcode.y.users.service.UserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    // Needs mapping
    @GetMapping
    public ResponseEntity<List<UserEntity>> getUsers() {
        List<UserEntity> users = userService.getUsers();
        // UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getPassword(), user.getCreatedAt());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        UserEntity user = userService.createUser(request.username(), request.password());
        UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(), user.getPasswordHash(), user.getCreatedAt());
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }
    
}
