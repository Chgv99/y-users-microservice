package com.chgvcode.y.users.auth.mapper;

import org.mapstruct.Mapper;

import com.chgvcode.y.users.auth.model.RefreshToken;
import com.chgvcode.y.users.auth.model.RefreshTokenEntity;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    public RefreshToken toModel(RefreshTokenEntity refreshTokenEntity);
}
