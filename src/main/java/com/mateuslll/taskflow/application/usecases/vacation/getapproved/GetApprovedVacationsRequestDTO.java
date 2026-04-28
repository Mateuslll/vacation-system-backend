package com.mateuslll.taskflow.application.usecases.vacation.getapproved;

import java.time.LocalDate;

public record GetApprovedVacationsRequestDTO(
        LocalDate startDate,
        LocalDate endDate
) {
    public GetApprovedVacationsRequestDTO() {
        this(null, null);
    }
}
