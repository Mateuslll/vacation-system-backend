package com.mateuslll.taskflow.application.usecases.user.collaborators;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record GetCollaboratorsByManagerRequestDTO(
        @NotNull(message = "managerId é obrigatório")
        UUID managerId
) {
}
