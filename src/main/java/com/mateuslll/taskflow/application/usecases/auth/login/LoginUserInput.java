package com.mateuslll.taskflow.application.usecases.auth.login;

public record LoginUserInput(
        String email,
        String password
) {
}
