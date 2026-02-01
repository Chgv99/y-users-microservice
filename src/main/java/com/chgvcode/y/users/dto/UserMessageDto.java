package com.chgvcode.y.users.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserMessageDto {
    private UUID uuid;
    private String username;
}
