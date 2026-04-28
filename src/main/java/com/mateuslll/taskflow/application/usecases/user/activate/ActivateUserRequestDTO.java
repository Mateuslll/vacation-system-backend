package com.mateuslll.taskflow.application.usecases.user.activate;

import io.swagger.v3.oas.annotations.Hidden;
import java.util.UUID;

public record ActivateUserRequestDTO(
        @Hidden
        UUID userId
) {
}
