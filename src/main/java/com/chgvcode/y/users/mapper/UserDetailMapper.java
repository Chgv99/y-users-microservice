package com.chgvcode.y.users.mapper;

import org.mapstruct.Mapper;

import com.chgvcode.y.users.dto.UserDetailResponse;
import com.chgvcode.y.users.model.UserDetail;
import com.chgvcode.y.users.model.UserDetailEntity;

@Mapper(componentModel = "spring")
public interface UserDetailMapper {

    public UserDetail toModel(UserDetailEntity userDetailEntity);

    public UserDetailResponse toResponse(UserDetail userDetail);
}
