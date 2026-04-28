package com.mateuslll.taskflow.application.usecases.vacation.getVacationRequestById;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GetVacationRequestByIdRequestDTO(
        @NotNull(message = "vacationRequestId não pode ser nulo")
        UUID vacationRequestId,

        @NotNull(message = "requesterId não pode ser nulo")
        UUID requesterId
) {
}
