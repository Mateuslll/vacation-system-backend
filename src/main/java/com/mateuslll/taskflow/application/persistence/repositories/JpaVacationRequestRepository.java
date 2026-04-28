package com.mateuslll.taskflow.application.persistence.repositories;

import com.mateuslll.taskflow.application.persistence.entities.vacation.VacationRequestEntity;
import com.mateuslll.taskflow.domain.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaVacationRequestRepository extends JpaRepository<VacationRequestEntity, UUID> {

    @Query("SELECT vr FROM VacationRequestEntity vr " +
           "LEFT JOIN FETCH vr.user " +
           "LEFT JOIN FETCH vr.approvedBy " +
           "WHERE vr.user.id = :userId")
    List<VacationRequestEntity> findByUserIdWithRelations(@Param("userId") UUID userId);

    @Query("SELECT vr FROM VacationRequestEntity vr " +
           "LEFT JOIN FETCH vr.user " +
           "WHERE vr.status = :status")
    List<VacationRequestEntity> findByStatus(@Param("status") RequestStatus status);

    @Query("SELECT vr FROM VacationRequestEntity vr " +
           "LEFT JOIN FETCH vr.user " +
           "WHERE vr.user.id = :userId AND vr.status = :status")
    List<VacationRequestEntity> findByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") RequestStatus status);

    @Query("SELECT vr FROM VacationRequestEntity vr " +
           "LEFT JOIN FETCH vr.user " +
           "WHERE vr.startDate >= :startDate AND vr.startDate <= :endDate")
    List<VacationRequestEntity> findByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT vr FROM VacationRequestEntity vr " +
           "LEFT JOIN FETCH vr.user " +
           "LEFT JOIN FETCH vr.approvedBy " +
           "WHERE vr.approvedBy.id = :managerId")
    List<VacationRequestEntity> findByApprovedBy(@Param("managerId") UUID managerId);

    @Query("SELECT vr FROM VacationRequestEntity vr " +
           "LEFT JOIN FETCH vr.user " +
           "LEFT JOIN FETCH vr.approvedBy " +
           "WHERE vr.id = :id")
    Optional<VacationRequestEntity> findByIdWithRelations(@Param("id") UUID id);

    @Query("SELECT vr FROM VacationRequestEntity vr " +
           "LEFT JOIN FETCH vr.user " +
           "LEFT JOIN FETCH vr.approvedBy " +
           "WHERE (:status IS NULL OR vr.status = :status) " +
           "AND (:userId IS NULL OR vr.user.id = :userId) " +
           "AND (:startDate IS NULL OR vr.endDate >= :startDate) " +
           "AND (:endDate IS NULL OR vr.startDate <= :endDate) " +
           "ORDER BY vr.createdAt DESC")
    List<VacationRequestEntity> findByFilters(
            @Param("status") RequestStatus status,
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT vr FROM VacationRequestEntity vr " +
           "LEFT JOIN FETCH vr.user " +
           "LEFT JOIN FETCH vr.approvedBy " +
           "WHERE vr.user.id IN :userIds " +
           "ORDER BY vr.createdAt DESC")
    List<VacationRequestEntity> findByUserIdIn(@Param("userIds") List<UUID> userIds);

    long countByUserIdAndStatus(UUID userId, RequestStatus status);

    @Query(value = "SELECT COALESCE(SUM(end_date - start_date + 1), 0) " +
                   "FROM vacation_requests " +
                   "WHERE user_id = :userId AND status = 'APPROVED'", 
           nativeQuery = true)
    Long sumApprovedDaysByUserId(@Param("userId") UUID userId);

    @Query("SELECT vr FROM VacationRequestEntity vr " +
           "LEFT JOIN FETCH vr.user " +
           "LEFT JOIN FETCH vr.approvedBy " +
           "WHERE vr.status IN ('PENDING', 'APPROVED') " +
           "AND (:excludeUserId IS NULL OR vr.user.id <> :excludeUserId) " +
           "AND ((vr.startDate <= :endDate AND vr.endDate >= :startDate))")
    List<VacationRequestEntity> findCrossUserOverlappingPendingOrApproved(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeUserId") UUID excludeUserId
    );

    @Query("SELECT vr FROM VacationRequestEntity vr "
           + "LEFT JOIN FETCH vr.user "
           + "LEFT JOIN FETCH vr.approvedBy "
           + "WHERE vr.user.id = :userId "
           + "AND vr.status IN ('PENDING', 'APPROVED') "
           + "AND vr.startDate <= :endDate AND vr.endDate >= :startDate "
           + "AND (:excludeVacationRequestId IS NULL OR vr.id <> :excludeVacationRequestId)")
    List<VacationRequestEntity> findOverlappingPendingOrApprovedForUser(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeVacationRequestId") UUID excludeVacationRequestId
    );
}
