package com.mateuslll.taskflow.application.usecases.vacation.getapproved;

import com.mateuslll.taskflow.application.usecases.UseCase;

import java.util.List;

public interface GetApprovedVacationsUseCase extends UseCase<GetApprovedVacationsRequestDTO, List<ApprovedVacationPeriodDTO>> {
}
