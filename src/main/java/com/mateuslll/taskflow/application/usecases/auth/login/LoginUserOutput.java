package com.mateuslll.taskflow.application.usecases.auth.login;

import lombok.Builder;

@Builder
public record LoginUserOutput(
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

public static LoginUserOutput of(String accessToken, String refreshToken, Long expiresIn, UserInfo user) {
        return LoginUserOutput.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(user)
                .build();
    }
}
