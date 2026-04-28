package com.mateuslll.taskflow.application.usecases.user.retrieve.byid;

import io.swagger.v3.oas.annotations.Hidden;
import java.util.UUID;

public record GetUserByIdRequestDTO(
        @Hidden
        UUID userId
) {
}
