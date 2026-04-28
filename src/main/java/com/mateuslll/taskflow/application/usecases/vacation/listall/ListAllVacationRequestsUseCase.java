package com.mateuslll.taskflow.application.usecases.vacation.listall;

import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;
import com.mateuslll.taskflow.domain.enums.RequestStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ListAllVacationRequestsUseCase {
    
List<VacationRequestResponseDTO> execute(
        RequestStatus status,
        UUID userId,
        LocalDate startDate,
        LocalDate endDate
    );
}
