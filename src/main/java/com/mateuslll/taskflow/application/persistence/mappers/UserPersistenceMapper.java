package com.mateuslll.taskflow.application.persistence.mappers;

import com.mateuslll.taskflow.application.persistence.entities.role.RoleEntity;
import com.mateuslll.taskflow.application.persistence.entities.user.UserEntity;
import com.mateuslll.taskflow.domain.entities.user.Role;
import com.mateuslll.taskflow.domain.entities.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

    @Mapping(target = "email", expression = "java(user.getEmail().value())")
    @Mapping(target = "passwordHash", expression = "java(user.getPassword().hashedValue())")
    @Mapping(target = "roles", source = "roles")
    UserEntity toEntity(User user);

    @Mapping(target = "users", ignore = true)
    RoleEntity toRoleEntity(Role role);
}
