package com.mateuslll.taskflow.application.usecases.vacation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record VacationRequestResponseDTO(
        UUID id,
        UUID userId,
        String userName,
        String userRole,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,
        Long days,
        String reason,
        String status,
        UUID approvedBy,
        String approvedByName,
        String rejectionReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime processedAt
) {
}
