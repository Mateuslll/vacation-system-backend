package com.mateuslll.taskflow.application.usecases.mappers;

import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;
import com.mateuslll.taskflow.domain.entities.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "name", expression = "java(user.getFullName())")
    @Mapping(target = "email", expression = "java(user.getEmail().value())")
    @Mapping(target = "status", expression = "java(user.getStatus().name())")
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(role -> role.getName().name()).collect(java.util.stream.Collectors.toSet()))")
    @Mapping(target = "lastLogin", source = "lastLoginAt")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    UserResponseDTO toResponse(User user);
}
