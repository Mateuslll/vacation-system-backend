package com.mateuslll.taskflow.application.usecases.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token é obrigatório")
        String refreshToken
) {
}
