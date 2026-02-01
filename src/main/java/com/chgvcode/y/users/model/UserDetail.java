package com.chgvcode.y.users.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDetail {
    private Long id;
    private UserEntity user;
    private String firstName;
    private String lastName;
}
