package com.mateuslll.taskflow.application.usecases.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterRequest(
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
        String password,

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
        String firstName,

        @NotBlank(message = "Sobrenome é obrigatório")
        @Size(min = 2, max = 50, message = "Sobrenome deve ter entre 2 e 50 caracteres")
        String lastName
) {
}
