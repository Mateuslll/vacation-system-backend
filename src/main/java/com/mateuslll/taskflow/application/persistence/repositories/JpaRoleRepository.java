package com.mateuslll.taskflow.application.persistence.repositories;

import com.mateuslll.taskflow.application.persistence.entities.role.RoleEntity;
import com.mateuslll.taskflow.domain.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRoleRepository extends JpaRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByName(RoleName name);

    boolean existsByName(RoleName name);
}
