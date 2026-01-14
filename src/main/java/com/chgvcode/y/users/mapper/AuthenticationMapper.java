package com.chgvcode.y.users.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.chgvcode.y.users.auth.dto.RegisterResponse;
import com.chgvcode.y.users.dto.RegisterUserResponse;

@Mapper(componentModel = "spring")
public interface AuthenticationMapper {
    @Mapping(source = "jwt", target = "jwt")
    public RegisterResponse toRegisterResponse(RegisterUserResponse registerUserResponse, String jwt);
}
