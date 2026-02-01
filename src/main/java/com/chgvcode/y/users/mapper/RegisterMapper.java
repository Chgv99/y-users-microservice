package com.chgvcode.y.users.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.chgvcode.y.users.auth.dto.AccessTokenDto;
import com.chgvcode.y.users.auth.dto.RegisterDto;
import com.chgvcode.y.users.auth.model.RefreshToken;
import com.chgvcode.y.users.model.User;

@Mapper(componentModel = "spring")
public interface RegisterMapper {
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    public RegisterDto toDto(User user, AccessTokenDto accessToken, RefreshToken refreshToken);
}
