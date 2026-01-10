package com.chgvcode.y.users.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chgvcode.y.users.dto.UserRequest;
import com.chgvcode.y.users.dto.UserResponse;
import com.chgvcode.y.users.service.IUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(userService.getUserByUsername(user.getUsername()));
    }

    @GetMapping
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) List<UUID> uuids,
            @RequestParam(required = false) List<String> usernames,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        if (uuids != null && !uuids.isEmpty()) {
            return ResponseEntity.ok(userService.getUsersByUuids(uuids));
        } else if (usernames != null && !usernames.isEmpty()) {
            return ResponseEntity.ok(userService.getUserListByUsernames(usernames));
        } else {
            String[] sortParams = sort.split(",");
            Pageable pageable = PageRequest.of(
                    page,
                    size,
                    Sort.by(Sort.Direction.fromString(sortParams[1]), sortParams[0]));
            return ResponseEntity.ok(userService.getUsers(pageable));
        }
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        UserResponse user = userService.createUser(request.username(),
                request.password());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deletePost(@PathVariable String username) {
        try {
            userService.deleteUser(username);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }  
}
