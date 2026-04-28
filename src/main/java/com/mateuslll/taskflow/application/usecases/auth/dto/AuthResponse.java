package com.mateuslll.taskflow.application.usecases.auth.dto;

import lombok.Builder;

@Builder
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        UserInfo user
) {

    @Builder
    public record UserInfo(
            String id,
            String email,
            String firstName,
            String lastName,
            String status
    ) {
    }
}
