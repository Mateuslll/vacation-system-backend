package com.mateuslll.taskflow.application.usecases.user.deactivate;

import io.swagger.v3.oas.annotations.Hidden;
import java.util.UUID;

public record DeactivateUserRequestDTO(
        @Hidden
        UUID userId
) {
}
