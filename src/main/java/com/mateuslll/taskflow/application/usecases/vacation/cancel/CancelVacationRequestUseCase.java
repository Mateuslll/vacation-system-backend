package com.mateuslll.taskflow.application.usecases.vacation.cancel;

import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;

import java.util.UUID;

public interface CancelVacationRequestUseCase {
    VacationRequestResponseDTO execute(UUID vacationRequestId, UUID requesterId);
}
