package com.mateuslll.taskflow.application.usecases.user.setroles;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SetRolesRequestDTO(
        @NotEmpty(message = "roles é obrigatório")
        List<String> roles
) {
}

