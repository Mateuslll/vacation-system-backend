package com.mateuslll.taskflow.application.usecases.auth.logout;

public record LogoutInput(
        String accessToken,
        String refreshToken
) {
}
