package com.mateuslll.taskflow.application.usecases.bootstrap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Request para criar administrador do sistema")
public record CreateAdminRequest(

        @Schema(description = "Primeiro nome do administrador", example = "Admin", required = true)
        @NotBlank(message = "Primeiro nome é obrigatório")
        @Size(min = 2, max = 50, message = "Primeiro nome deve ter entre 2 e 50 caracteres")
        String firstName,

        @Schema(description = "Sobrenome do administrador", example = "Sistema", required = true)
        @NotBlank(message = "Sobrenome é obrigatório")
        @Size(min = 2, max = 50, message = "Sobrenome deve ter entre 2 e 50 caracteres")
        String lastName,

        @Schema(description = "Email do administrador", example = "admin@taskflow.com", required = true)
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        String email,

        @Schema(
                description = "Senha do administrador (mínimo 8 caracteres, deve conter maiúscula, minúscula, número e caractere especial)",
                example = "Admin@123",
                required = true
        )
        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$",
                message = "Senha deve conter ao menos: 1 maiúscula, 1 minúscula, 1 número e 1 caractere especial"
        )
        String password
) {
}
