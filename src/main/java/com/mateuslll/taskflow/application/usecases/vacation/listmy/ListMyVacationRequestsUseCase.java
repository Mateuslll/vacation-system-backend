package com.mateuslll.taskflow.application.usecases.vacation.listmy;

import com.mateuslll.taskflow.application.usecases.UseCase;
import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ListMyVacationRequestsUseCase {
    
List<VacationRequestResponseDTO> execute(UUID userId);
}
