package com.chgvcode.y.users.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.chgvcode.y.users.dto.RegisterUserResponse;
import com.chgvcode.y.users.dto.UserResponse;
import com.chgvcode.y.users.model.User;
import com.chgvcode.y.users.model.UserDetailEntity;
import com.chgvcode.y.users.model.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

    public User toModel(UserEntity userEntity);
    
    public UserResponse toUserResponse(UserEntity userEntity);

    @Mapping(source = "user.uuid", target = "uuid")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "userDetail.firstName", target = "firstName")
    @Mapping(source = "userDetail.lastName", target = "lastName")
    @Mapping(source = "user.createdAt", target = "createdAt")
    RegisterUserResponse toRegisterUserResponse(UserEntity user, UserDetailEntity userDetail);

}
