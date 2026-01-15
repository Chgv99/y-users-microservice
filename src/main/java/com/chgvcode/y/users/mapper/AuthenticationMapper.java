package com.chgvcode.y.users.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.chgvcode.y.users.auth.dto.TokenResponse;
import com.chgvcode.y.users.auth.dto.RegisterResponse;
import com.chgvcode.y.users.dto.RegisterUserResponse;

@Mapper(componentModel = "spring")
public interface AuthenticationMapper {
    @Mapping(source = "tokenResponse", target = "tokenResponse")
    public RegisterResponse toRegisterResponse(RegisterUserResponse registerUserResponse, TokenResponse tokenResponse);
}
