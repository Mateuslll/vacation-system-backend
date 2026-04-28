package com.mateuslll.taskflow.application.usecases.bootstrap;

public record CreateAdminInput(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
