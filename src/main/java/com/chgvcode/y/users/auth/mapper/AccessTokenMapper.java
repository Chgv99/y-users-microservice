package com.chgvcode.y.users.auth.mapper;

import org.mapstruct.Mapper;

import com.chgvcode.y.users.auth.dto.AccessTokenDto;
import com.chgvcode.y.users.auth.dto.AccessTokenResponse;

@Mapper(componentModel = "spring")
public interface AccessTokenMapper {
    public AccessTokenResponse toResponse(AccessTokenDto dto);
}
