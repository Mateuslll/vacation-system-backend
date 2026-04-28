package com.mateuslll.taskflow.application.usecases.vacation.listteam;

import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ListTeamVacationRequestsUseCase {
    
List<VacationRequestResponseDTO> execute(UUID requesterId);
}
