package com.mateuslll.taskflow.application.usecases.bootstrap;

import java.util.UUID;

public record CreateAdminOutput(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String message
) {
}
