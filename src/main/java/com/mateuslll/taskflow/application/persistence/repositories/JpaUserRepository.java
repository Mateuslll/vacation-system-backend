package com.mateuslll.taskflow.application.persistence.repositories;

import com.mateuslll.taskflow.application.persistence.entities.user.UserEntity;
import com.mateuslll.taskflow.domain.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<UserEntity> findByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    boolean existsByIdAndManagerId(UUID id, UUID managerId);

    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<UserEntity> findByIdWithRoles(@Param("id") UUID id);

    @Query("SELECT DISTINCT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.id IN :ids")
    List<UserEntity> findAllByIdIn(@Param("ids") Collection<UUID> ids);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.status = :status")
    List<UserEntity> findByStatusWithRoles(@Param("status") UserStatus status);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.managerId = :managerId")
    List<UserEntity> findByManagerId(@Param("managerId") UUID managerId);
}
