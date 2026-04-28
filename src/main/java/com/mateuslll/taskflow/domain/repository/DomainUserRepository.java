package com.mateuslll.taskflow.domain.repository;

import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.enums.UserStatus;
import com.mateuslll.taskflow.domain.valueobject.Email;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DomainUserRepository {

User save(User user);

Optional<User> findById(UUID id);

List<User> findAllByIdIn(Collection<UUID> ids);

Optional<User> findByEmail(Email email);

boolean existsByEmail(Email email);

boolean existsByIdAndManagerId(UUID userId, UUID managerId);

List<User> findAll();

List<User> findByStatus(UserStatus status);

List<User> findByManagerId(UUID managerId);

void deleteById(UUID id);
}
