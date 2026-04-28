package com.mateuslll.taskflow.application.usecases.user.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UpdateUserRequestDTO(
        
        UUID userId,
        
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
        String firstName,
        
        @NotBlank(message = "Sobrenome é obrigatório")
        @Size(min = 2, max = 50, message = "Sobrenome deve ter entre 2 e 50 caracteres")
        String lastName,
        
        @Size(max = 100, message = "Departamento deve ter no máximo 100 caracteres")
        String department,
        
        @Size(max = 100, message = "Cargo deve ter no máximo 100 caracteres")
        String position
) {
}
