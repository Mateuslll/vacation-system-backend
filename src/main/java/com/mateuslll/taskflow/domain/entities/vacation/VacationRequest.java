package com.mateuslll.taskflow.domain.entities.vacation;

import com.mateuslll.taskflow.common.exceptions.*;
import com.mateuslll.taskflow.common.messages.ResourceMessages;
import com.mateuslll.taskflow.domain.enums.RequestStatus;
import com.mateuslll.taskflow.domain.valueobject.DateRange;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id")
public class VacationRequest {

    private UUID id;
    private UUID userId;
    private DateRange period;
    private String reason;
    private RequestStatus status;
    private UUID approvedBy;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime processedAt;

public VacationRequest(UUID userId, DateRange period, String reason) {
        this.id = UUID.randomUUID();
        this.userId = validateUserId(userId);
        this.period = validatePeriod(period);
        this.reason = validateReason(reason);
        this.status = RequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

public VacationRequest(UUID id, UUID userId, DateRange period, String reason,
                          RequestStatus status, UUID approvedBy, String rejectionReason,
                          LocalDateTime createdAt, LocalDateTime updatedAt, 
                          LocalDateTime processedAt) {
        this.id = id;
        this.userId = userId;
        this.period = period;
        this.reason = reason;
        this.status = status;
        this.approvedBy = approvedBy;
        this.rejectionReason = rejectionReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.processedAt = processedAt;
    }

    public void approve(UUID managerId) {
        if (managerId == null) {
            throw new BadRequestException(ResourceMessages.FIELD_REQUIRED.format("ID do gerente"));
        }

        if (!this.status.canBeModified()) {
            throw new VacationAlreadyProcessedException(this.id.toString());
        }

        if (this.period.isPast()) {
            throw new VacationAlreadyStartedException(this.id.toString());
        }

        this.status = RequestStatus.APPROVED;
        this.approvedBy = managerId;
        this.processedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(UUID managerId, String rejectionReason) {
        if (managerId == null) {
            throw new BadRequestException(ResourceMessages.FIELD_REQUIRED.format("ID do gerente"));
        }

        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new InvalidRejectionReasonException(ResourceMessages.REJECTION_REASON_REQUIRED.getMessage());
        }

        if (rejectionReason.trim().length() < 10) {
            throw new InvalidRejectionReasonException(ResourceMessages.REJECTION_REASON_TOO_SHORT.getMessage());
        }

        if (!this.status.canBeModified()) {
            throw new VacationAlreadyProcessedException(this.id.toString());
        }

        this.status = RequestStatus.REJECTED;
        this.approvedBy = managerId;
        this.rejectionReason = rejectionReason.trim();
        this.processedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel(UUID requestingUserId) {
        if (requestingUserId == null) {
            throw new BadRequestException(ResourceMessages.FIELD_REQUIRED.format("ID do usuário"));
        }

        if (!this.userId.equals(requestingUserId)) {
            throw new VacationCancellationNotAllowedException(requestingUserId.toString(), this.id.toString());
        }

        if (!this.status.canBeModified()) {
            throw new VacationAlreadyProcessedException(this.id.toString());
        }

        if (this.period.isPast()) {
            throw new VacationAlreadyStartedException(this.id.toString());
        }

        this.status = RequestStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void update(DateRange newPeriod, String newReason) {
        if (!this.status.canBeModified()) {
            throw new VacationAlreadyProcessedException(this.id.toString());
        }

        if (newPeriod != null) {
            this.period = validatePeriod(newPeriod);
        }

        if (newReason != null && !newReason.isBlank()) {
            this.reason = validateReason(newReason);
        }

        this.updatedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return this.status.isPending();
    }

    public boolean canBeModified() {
        return this.status.canBeModified();
    }

    public long getDays() {
        return this.period.getDays();
    }

    public boolean overlapsWith(VacationRequest other) {
        if (other == null || !other.userId.equals(this.userId)) {
            return false;
        }
        return this.period.overlaps(other.period);
    }

    private UUID validateUserId(UUID userId) {
        if (userId == null) {
            throw new DomainException(ResourceMessages.FIELD_REQUIRED.format("ID do usuário"));
        }
        return userId;
    }

    private DateRange validatePeriod(DateRange period) {
        if (period == null) {
            throw new DomainException(ResourceMessages.FIELD_REQUIRED.format("Período de férias"));
        }

        if (!period.isInFuture()) {
            throw new DomainException(ResourceMessages.VACATION_PERIOD_MUST_BE_FUTURE.getMessage());
        }

        if (period.getDays() < 5) {
            throw new DomainException(ResourceMessages.VACATION_MINIMUM_DAYS.format(5));
        }

        if (period.getDays() > 30) {
            throw new DomainException(ResourceMessages.VACATION_MAXIMUM_DAYS.format(30));
        }

        return period;
    }

    private String validateReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new DomainException(ResourceMessages.FIELD_REQUIRED.format("Motivo"));
        }

        String trimmed = reason.trim();
        
        if (trimmed.length() < 10) {
            throw new DomainException(ResourceMessages.VACATION_REASON_TOO_SHORT.getMessage());
        }

        if (trimmed.length() > 500) {
            throw new DomainException(ResourceMessages.VACATION_REASON_TOO_LONG.getMessage());
        }

        return trimmed;
    }

    @Override
    public String toString() {
        return String.format("VacationRequest{id=%s, userId=%s, period=%s, status=%s, days=%d}",
                id, userId, period, status, getDays());
    }
}
