package com.chgvcode.y.users.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserMessage {
    private UUID uuid;
    private String username;
}
