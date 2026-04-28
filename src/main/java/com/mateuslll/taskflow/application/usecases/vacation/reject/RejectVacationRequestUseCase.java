package com.mateuslll.taskflow.application.usecases.vacation.reject;

import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;

import java.util.UUID;

public interface RejectVacationRequestUseCase {
    VacationRequestResponseDTO execute(UUID vacationRequestId, UUID managerId, String rejectionReason);
}
