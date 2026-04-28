package com.mateuslll.taskflow.application.usecases.vacation.getapproved;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.UUID;

public record ApprovedVacationPeriodDTO(
        UUID vacationRequestId,
        UUID userId,
        String userName,
        String userEmail,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,
        Integer totalDays,
        String status
) {
    public static ApprovedVacationPeriodDTO of(
            UUID vacationRequestId,
            UUID userId,
            String userName,
            String userEmail,
            LocalDate startDate,
            LocalDate endDate,
            Integer totalDays,
            String status) {
        return new ApprovedVacationPeriodDTO(
                vacationRequestId,
                userId,
                userName,
                userEmail,
                startDate,
                endDate,
                totalDays,
                status
        );
    }
}
