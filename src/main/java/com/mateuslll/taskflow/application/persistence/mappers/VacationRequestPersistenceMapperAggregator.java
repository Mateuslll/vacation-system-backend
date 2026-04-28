package com.mateuslll.taskflow.application.persistence.mappers;

import com.mateuslll.taskflow.application.persistence.entities.vacation.VacationRequestEntity;
import com.mateuslll.taskflow.domain.entities.vacation.VacationRequest;
import com.mateuslll.taskflow.domain.valueobject.DateRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VacationRequestPersistenceMapperAggregator {

    private final VacationRequestPersistenceMapper mapper;

public VacationRequestEntity toEntity(VacationRequest domain) {
        return mapper.toEntity(domain);
    }

public VacationRequest toDomain(VacationRequestEntity entity) {
        if (entity == null) {
            return null;
        }

        UUID userId = entity.getUser() != null ? entity.getUser().getId() : null;
        UUID approvedById = entity.getApprovedBy() != null ? entity.getApprovedBy().getId() : null;
        
        DateRange period = new DateRange(entity.getStartDate(), entity.getEndDate());

        return new VacationRequest(
                entity.getId(),
                userId,
                period,
                entity.getReason(),
                entity.getStatus(),
                approvedById,
                entity.getRejectionReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getProcessedAt()
        );
    }
}
