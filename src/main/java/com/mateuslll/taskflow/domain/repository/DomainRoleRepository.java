package com.mateuslll.taskflow.domain.repository;

import com.mateuslll.taskflow.domain.entities.user.Role;
import com.mateuslll.taskflow.domain.enums.RoleName;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DomainRoleRepository {

Role save(Role role);

Optional<Role> findById(UUID id);

Optional<Role> findByName(RoleName name);
}
