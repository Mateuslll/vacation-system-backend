package com.mateuslll.taskflow.application.usecases.vacation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mateuslll.taskflow.application.usecases.vacation.create.dto.CreateVacationRequestRequestDTO;
import com.mateuslll.taskflow.application.usecases.vacation.getapproved.ApprovedVacationPeriodDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Vacation Period Date-Only Contract Test")
class VacationPeriodDateOnlyContractTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    @DisplayName("Deve desserializar request de férias com formato yyyy-MM-dd")
    void shouldDeserializeVacationRequestWithDateOnlyFormat() throws Exception {
        String json = """
                {
                  "startDate": "2026-05-10",
                  "endDate": "2026-05-14",
                  "reason": "Férias planejadas com antecedência"
                }
                """;

        CreateVacationRequestRequestDTO request = objectMapper.readValue(json, CreateVacationRequestRequestDTO.class);

        assertThat(request.startDate()).isEqualTo(LocalDate.of(2026, 5, 10));
        assertThat(request.endDate()).isEqualTo(LocalDate.of(2026, 5, 14));
    }

    @Test
    @DisplayName("Deve rejeitar hora no período da request de férias")
    void shouldRejectDateTimeInVacationRequestPeriod() {
        String json = """
                {
                  "startDate": "2026-05-10T00:00:00",
                  "endDate": "2026-05-14",
                  "reason": "Férias planejadas com antecedência"
                }
                """;

        assertThatThrownBy(() -> objectMapper.readValue(json, CreateVacationRequestRequestDTO.class))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Deve serializar período de férias sem hora no response principal")
    void shouldSerializeVacationResponsePeriodWithoutTime() throws Exception {
        VacationRequestResponseDTO response = new VacationRequestResponseDTO(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "User Test",
                "USER",
                LocalDate.of(2026, 5, 10),
                LocalDate.of(2026, 5, 14),
                5L,
                "Férias planejadas",
                "PENDING",
                null,
                null,
                null,
                LocalDateTime.of(2026, 4, 26, 12, 0),
                LocalDateTime.of(2026, 4, 26, 12, 30),
                null
        );

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"startDate\":\"2026-05-10\"");
        assertThat(json).contains("\"endDate\":\"2026-05-14\"");
        assertThat(json).doesNotContain("\"startDate\":\"2026-05-10T");
        assertThat(json).doesNotContain("\"endDate\":\"2026-05-14T");
    }

    @Test
    @DisplayName("Deve serializar período de férias aprovadas sem hora")
    void shouldSerializeApprovedVacationPeriodWithoutTime() throws Exception {
        ApprovedVacationPeriodDTO response = ApprovedVacationPeriodDTO.of(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "User Test",
                "user@test.com",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 5),
                5,
                "APPROVED"
        );

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"startDate\":\"2026-06-01\"");
        assertThat(json).contains("\"endDate\":\"2026-06-05\"");
        assertThat(json).doesNotContain("\"startDate\":\"2026-06-01T");
        assertThat(json).doesNotContain("\"endDate\":\"2026-06-05T");
    }
}
