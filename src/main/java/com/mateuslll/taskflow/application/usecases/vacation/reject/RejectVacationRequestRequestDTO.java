package com.mateuslll.taskflow.application.usecases.vacation.reject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectVacationRequestRequestDTO(
        @NotBlank(message = "rejectionReason não pode estar em branco")
        @Size(min = 10, message = "rejectionReason deve ter no mínimo 10 caracteres")
        String rejectionReason
) {
}
