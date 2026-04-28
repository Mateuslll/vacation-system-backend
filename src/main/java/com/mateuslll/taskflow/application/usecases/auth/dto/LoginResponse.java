package com.mateuslll.taskflow.application.usecases.auth.dto;

import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn
) {
}
