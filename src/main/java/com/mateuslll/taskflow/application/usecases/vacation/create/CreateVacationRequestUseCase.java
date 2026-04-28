package com.mateuslll.taskflow.application.usecases.vacation.create;

import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;
import com.mateuslll.taskflow.application.usecases.vacation.create.dto.CreateVacationRequestRequestDTO;

import java.util.UUID;

public interface CreateVacationRequestUseCase {

    VacationRequestResponseDTO execute(UUID requesterId, CreateVacationRequestRequestDTO request);
}
