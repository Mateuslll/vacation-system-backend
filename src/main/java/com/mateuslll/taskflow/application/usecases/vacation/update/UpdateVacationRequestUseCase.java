package com.mateuslll.taskflow.application.usecases.vacation.update;

import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;

import java.util.UUID;

public interface UpdateVacationRequestUseCase {
    VacationRequestResponseDTO execute(UUID vacationRequestId, UUID requesterId, UpdateVacationRequestRequestDTO request);
}
