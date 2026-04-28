package com.mateuslll.taskflow.application.usecases.auth.register;

import lombok.Builder;

@Builder
public record RegisterUserOutput(
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

public static RegisterUserOutput of(String accessToken, String refreshToken, Long expiresIn, UserInfo user) {
        return RegisterUserOutput.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(user)
                .build();
    }
}
