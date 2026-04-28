package com.mateuslll.taskflow.application.usecases.vacation.approve;

import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;

import java.util.UUID;

public interface ApproveVacationRequestUseCase {
    VacationRequestResponseDTO execute(UUID vacationRequestId, UUID managerId);
}
