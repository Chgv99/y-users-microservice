package com.chgvcode.y.users.mapper;

import org.mapstruct.Mapper;

import com.chgvcode.y.users.dto.UserResponse;
import com.chgvcode.y.users.model.User;
import com.chgvcode.y.users.model.UserEntity;

@Mapper(componentModel = "spring", uses = UserDetailMapper.class)
public interface UserMapper {

    public User toModel(UserEntity userEntity);
    
    public UserResponse toResponse(UserEntity userEntity);
    
    public UserResponse toResponse(User user);
}
