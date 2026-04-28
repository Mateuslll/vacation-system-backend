package com.mateuslll.taskflow.application.usecases.user.assignmanager;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignManagerRequestDTO(
        @NotNull(message = "userId é obrigatório")
        UUID userId,

        @NotNull(message = "managerId é obrigatório")
        UUID managerId
) {
}
