package com.mateuslll.taskflow.application.usecases.user.assignrole;

import jakarta.validation.constraints.NotBlank;

public record AssignRoleRequestDTO(
        @NotBlank(message = "roleName é obrigatório")
        String roleName
) {
}
