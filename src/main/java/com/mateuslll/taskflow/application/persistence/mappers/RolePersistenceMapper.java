package com.mateuslll.taskflow.application.persistence.mappers;

import com.mateuslll.taskflow.application.persistence.entities.role.RoleEntity;
import com.mateuslll.taskflow.domain.entities.user.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public abstract class RolePersistenceMapper {

    @Mapping(target = "users", ignore = true)
    public abstract RoleEntity toEntity(Role role);

    public abstract Role toDomain(RoleEntity entity);

    @ObjectFactory
    protected Role createRole(RoleEntity entity) {
        return new Role(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getPermissions()
        );
    }
}
