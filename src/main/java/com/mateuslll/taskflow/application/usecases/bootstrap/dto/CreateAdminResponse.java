package com.mateuslll.taskflow.application.usecases.bootstrap.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response da criação de administrador")
public record CreateAdminResponse(

        @Schema(description = "ID do administrador criado", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Primeiro nome", example = "Admin")
        String firstName,

        @Schema(description = "Sobrenome", example = "Sistema")
        String lastName,

        @Schema(description = "Email", example = "admin@taskflow.com")
        String email,

        @Schema(description = "Mensagem de sucesso", example = "Administrador criado com sucesso")
        String message
) {
}
