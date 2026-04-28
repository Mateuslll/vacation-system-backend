package com.mateuslll.taskflow.application.persistence.mappers;

import com.mateuslll.taskflow.application.persistence.entities.user.UserEntity;
import com.mateuslll.taskflow.domain.entities.user.Role;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.valueobject.Email;
import com.mateuslll.taskflow.domain.valueobject.Password;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserPersistenceMapperAggregator {

    private final UserPersistenceMapper persistenceMapper;
    private final RolePersistenceMapper rolePersistenceMapper;

    public UserEntity toEntity(User user) {
        return persistenceMapper.toEntity(user);
    }

    public User toDomain(UserEntity entity) {
        Email email = new Email(entity.getEmail());
        Password password = Password.fromHash(entity.getPasswordHash());

        Set<Role> roles = entity.getRoles().stream()
                .map(rolePersistenceMapper::toDomain)
                .collect(Collectors.toSet());

        return new User(
                entity.getId(),
                email,
                password,
                entity.getFirstName(),
                entity.getLastName(),
                entity.getDepartment(),
                entity.getPosition(),
                entity.getManagerId(),
                entity.getStatus(),
                roles,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getLastLoginAt()
        );
    }

}
