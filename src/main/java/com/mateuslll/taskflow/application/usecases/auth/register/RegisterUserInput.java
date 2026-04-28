package com.mateuslll.taskflow.application.usecases.auth.register;

public record RegisterUserInput(
        String email,
        String password,
        String firstName,
        String lastName
) {
}
