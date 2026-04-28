package com.mateuslll.taskflow.application.persistence.repositories;

import com.mateuslll.taskflow.application.persistence.entities.user.UserEntity;
import com.mateuslll.taskflow.application.persistence.entities.vacation.VacationRequestEntity;
import com.mateuslll.taskflow.application.persistence.mappers.VacationRequestPersistenceMapperAggregator;
import com.mateuslll.taskflow.domain.entities.vacation.VacationRequest;
import com.mateuslll.taskflow.domain.enums.RequestStatus;
import com.mateuslll.taskflow.domain.repository.DomainVacationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostgresVacationRequestRepository implements DomainVacationRequestRepository {

    private final JpaVacationRequestRepository jpaRepository;
    private final JpaUserRepository jpaUserRepository;
    private final VacationRequestPersistenceMapperAggregator mapper;

    @Override
    public VacationRequest save(VacationRequest request) {
        VacationRequestEntity entity = mapper.toEntity(request);
        
        UserEntity userEntity = jpaUserRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));
        entity.setUser(userEntity);
        
        if (request.getApprovedBy() != null) {
            UserEntity approvedByEntity = jpaUserRepository.findById(request.getApprovedBy())
                    .orElseThrow(() -> new IllegalArgumentException("Manager not found: " + request.getApprovedBy()));
            entity.setApprovedBy(approvedByEntity);
        }
        
        VacationRequestEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<VacationRequest> findById(UUID id) {
        return jpaRepository.findByIdWithRelations(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<VacationRequest> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<VacationRequest> findByUserId(UUID userId) {
        return jpaRepository.findByUserIdWithRelations(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<VacationRequest> findByStatus(RequestStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<VacationRequest> findByUserIdAndStatus(UUID userId, RequestStatus status) {
        return jpaRepository.findByUserIdAndStatus(userId, status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<VacationRequest> findByFilters(
            RequestStatus status,
            UUID userId,
            LocalDate startDate,
            LocalDate endDate) {
        return jpaRepository.findByFilters(status, userId, startDate, endDate).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<VacationRequest> findByUserIds(List<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findByUserIdIn(userIds).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<VacationRequest> findCrossUserOverlappingPendingOrApproved(
            LocalDate startDate,
            LocalDate endDate,
            UUID excludeUserId) {
        return jpaRepository.findCrossUserOverlappingPendingOrApproved(startDate, endDate, excludeUserId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<VacationRequest> findOverlappingPendingOrApprovedForUser(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate,
            UUID excludeVacationRequestId) {
        return jpaRepository.findOverlappingPendingOrApprovedForUser(userId, startDate, endDate, excludeVacationRequestId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
