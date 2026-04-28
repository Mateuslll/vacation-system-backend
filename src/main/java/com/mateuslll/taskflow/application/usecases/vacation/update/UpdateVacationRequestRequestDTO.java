package com.mateuslll.taskflow.application.usecases.vacation.update;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateVacationRequestRequestDTO(
        @Schema(type = "string", format = "date", example = "2026-06-01")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "startDate não pode ser nula")
        @FutureOrPresent(message = "startDate deve ser presente ou futura")
        LocalDate startDate,

        @Schema(type = "string", format = "date", example = "2026-06-10")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "endDate não pode ser nula")
        LocalDate endDate,

        @NotBlank(message = "reason não pode estar em branco")
        String reason
) {
}
