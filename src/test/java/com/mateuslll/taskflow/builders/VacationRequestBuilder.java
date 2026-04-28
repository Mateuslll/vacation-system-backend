package com.mateuslll.taskflow.builders;

import com.mateuslll.taskflow.domain.entities.vacation.VacationRequest;
import com.mateuslll.taskflow.domain.enums.RequestStatus;
import com.mateuslll.taskflow.domain.valueobject.DateRange;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


public class VacationRequestBuilder {

    private UUID id = null;
    private UUID userId = UUID.randomUUID();
    private LocalDate startDate = LocalDate.now().plusDays(7);
    private LocalDate endDate = LocalDate.now().plusDays(11);
    private String reason = "Vacation for rest and relaxation with family";
    private RequestStatus status = RequestStatus.PENDING;
    private UUID approvedBy = null;
    private String rejectionReason = null;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime processedAt = null;

    public static VacationRequestBuilder aVacationRequest() {
        return new VacationRequestBuilder();
    }

    public static VacationRequest pendingVacation() {
        return aVacationRequest().build();
    }

    public static VacationRequest approvedVacation() {
        return aVacationRequest()
                .withStatus(RequestStatus.APPROVED)
                .withApprovedBy(UUID.randomUUID())
                .withProcessedAt(LocalDateTime.now())
                .build();
    }

    public static VacationRequest rejectedVacation() {
        return aVacationRequest()
                .withStatus(RequestStatus.REJECTED)
                .withApprovedBy(UUID.randomUUID())
                .withRejectionReason("Not enough vacation days available")
                .withProcessedAt(LocalDateTime.now())
                .build();
    }

    public static VacationRequest cancelledVacation() {
        return aVacationRequest()
                .withStatus(RequestStatus.CANCELLED)
                .build();
    }

    public VacationRequestBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public VacationRequestBuilder withUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public VacationRequestBuilder withPeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        return this;
    }

    public VacationRequestBuilder withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public VacationRequestBuilder withEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public VacationRequestBuilder nextWeek() {
        this.startDate = LocalDate.now().plusDays(7);
        this.endDate = LocalDate.now().plusDays(11);
        return this;
    }

    public VacationRequestBuilder nextMonth() {
        this.startDate = LocalDate.now().plusMonths(1);
        this.endDate = LocalDate.now().plusMonths(1).plusDays(9);
        return this;
    }

    public VacationRequestBuilder withReason(String reason) {
        this.reason = reason;
        return this;
    }

    public VacationRequestBuilder withStatus(RequestStatus status) {
        this.status = status;
        return this;
    }

    public VacationRequestBuilder withApprovedBy(UUID approvedBy) {
        this.approvedBy = approvedBy;
        return this;
    }

    public VacationRequestBuilder withRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
        return this;
    }

    public VacationRequestBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public VacationRequestBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public VacationRequestBuilder withProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
        return this;
    }

    public VacationRequest build() {
        DateRange period = new DateRange(startDate, endDate);

        if (id != null) {
            return new VacationRequest(
                    id, userId, period, reason, status,
                    approvedBy, rejectionReason,
                    createdAt, updatedAt, processedAt
            );
        }

        return new VacationRequest(userId, period, reason);
    }
}
