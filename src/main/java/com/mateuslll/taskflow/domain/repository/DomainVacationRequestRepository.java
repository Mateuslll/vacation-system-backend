package com.mateuslll.taskflow.domain.repository;

import com.mateuslll.taskflow.domain.entities.vacation.VacationRequest;
import com.mateuslll.taskflow.domain.enums.RequestStatus;
import com.mateuslll.taskflow.domain.valueobject.DateRange;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DomainVacationRequestRepository {

    VacationRequest save(VacationRequest request);

    Optional<VacationRequest> findById(UUID id);

    List<VacationRequest> findAll();

    List<VacationRequest> findByUserId(UUID userId);

    List<VacationRequest> findByStatus(RequestStatus status);

    List<VacationRequest> findByUserIdAndStatus(UUID userId, RequestStatus status);

    List<VacationRequest> findByFilters(
            RequestStatus status,
            UUID userId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<VacationRequest> findByUserIds(List<UUID> userIds);

    /**
     * Pedidos de <strong>outros</strong> utilizadores com status {@code PENDING} ou {@code APPROVED} cujo
     * período (datas inclusivas) intersecta {@code [startDate, endDate]}. Exclui o utilizador
     * {@code excludeUserId} (tipicamente o solicitante).
     */
    List<VacationRequest> findCrossUserOverlappingPendingOrApproved(
            LocalDate startDate,
            LocalDate endDate,
            UUID excludeUserId
    );

    /**
     * Pedidos do utilizador {@code userId} com status {@code PENDING} ou {@code APPROVED} sobrepostos
     * (inclusivo) a {@code [startDate, endDate]}. {@code excludeVacationRequestId} pode ser {@code null}
     * (criação).
     */
    List<VacationRequest> findOverlappingPendingOrApprovedForUser(
            UUID userId,
            LocalDate startDate,
            LocalDate endDate,
            UUID excludeVacationRequestId
    );
}
