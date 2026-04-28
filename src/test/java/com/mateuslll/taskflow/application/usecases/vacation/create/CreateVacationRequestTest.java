package com.mateuslll.taskflow.application.usecases.vacation.create;

import com.mateuslll.taskflow.application.usecases.mappers.VacationRequestMapper;
import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;
import com.mateuslll.taskflow.application.usecases.vacation.create.dto.CreateVacationRequestRequestDTO;
import com.mateuslll.taskflow.builders.RoleBuilder;
import com.mateuslll.taskflow.builders.UserBuilder;
import com.mateuslll.taskflow.common.exceptions.BadRequestException;
import com.mateuslll.taskflow.common.exceptions.ForbiddenAccessException;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.entities.vacation.VacationRequest;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import com.mateuslll.taskflow.domain.repository.DomainVacationRequestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateVacationRequest Test")
class CreateVacationRequestTest {

    @Mock
    private DomainVacationRequestRepository vacationRequestRepository;

    @Mock
    private DomainUserRepository userRepository;

    @Mock
    private VacationRequestMapper mapper;

    @InjectMocks
    private CreateVacationRequest createVacationRequest;

    @Test
    @DisplayName("Deve bloquear criação de férias para ADMIN")
    void shouldBlockVacationCreationForAdmin() {
        UUID userId = UUID.randomUUID();

        User adminUser = UserBuilder.aUser()
                .withId(userId)
                .withRole(RoleBuilder.adminRole())
                .build();

        CreateVacationRequestRequestDTO request = validRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.of(adminUser));

        assertThatThrownBy(() -> createVacationRequest.execute(userId, request))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessageContaining("ADMIN");

        verify(vacationRequestRepository, never()).save(any(VacationRequest.class));
    }

    @Test
    @DisplayName("Deve bloquear criação de férias para MANAGER")
    void shouldBlockVacationCreationForManager() {
        UUID userId = UUID.randomUUID();

        User managerUser = UserBuilder.aUser()
                .withId(userId)
                .withRole(RoleBuilder.managerRole())
                .build();

        CreateVacationRequestRequestDTO request = validRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.of(managerUser));

        assertThatThrownBy(() -> createVacationRequest.execute(userId, request))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessageContaining("MANAGER");

        verify(vacationRequestRepository, never()).save(any(VacationRequest.class));
    }

    @Test
    @DisplayName("Deve bloquear criação para usuário promovido para MANAGER")
    void shouldBlockVacationCreationForPromotedManager() {
        UUID userId = UUID.randomUUID();

        User promotedUser = UserBuilder.aUser()
                .withId(userId)
                .withRole(RoleBuilder.userRole())
                .withRole(RoleBuilder.managerRole())
                .build();

        CreateVacationRequestRequestDTO request = validRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.of(promotedUser));

        assertThatThrownBy(() -> createVacationRequest.execute(userId, request))
                .isInstanceOf(ForbiddenAccessException.class)
                .hasMessageContaining("MANAGER");

        verify(vacationRequestRepository, never()).save(any(VacationRequest.class));
    }

    @Test
    @DisplayName("Deve rejeitar criação de férias sem gestor associado")
    void shouldRejectVacationCreationWithoutManager() {
        UUID userId = UUID.randomUUID();

        User user = UserBuilder.aUser()
                .withId(userId)
                .withRole(RoleBuilder.userRole())
                .build();

        CreateVacationRequestRequestDTO request = validRequest();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> createVacationRequest.execute(userId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("gestor");

        verify(vacationRequestRepository, never()).save(any(VacationRequest.class));
    }

    @Test
    @DisplayName("Deve permitir criação de férias para USER")
    void shouldAllowVacationCreationForUserRole() {
        UUID userId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        User user = UserBuilder.aUser()
                .withId(userId)
                .withManagerId(managerId)
                .withRole(RoleBuilder.userRole())
                .build();

        CreateVacationRequestRequestDTO request = validRequest();

        VacationRequestResponseDTO expectedResponse = new VacationRequestResponseDTO(
                UUID.randomUUID(),
                userId,
                user.getFullName(),
                "USER",
                request.startDate(),
                request.endDate(),
                5L,
                request.reason(),
                "PENDING",
                null,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(vacationRequestRepository.findOverlappingPendingOrApprovedForUser(
                userId, request.startDate(), request.endDate(), null))
                .thenReturn(List.of());
        when(vacationRequestRepository.findCrossUserOverlappingPendingOrApproved(
                request.startDate(), request.endDate(), userId))
                .thenReturn(List.of());
        when(vacationRequestRepository.save(any(VacationRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(VacationRequest.class), any(User.class), any()))
                .thenReturn(expectedResponse);

        VacationRequestResponseDTO response = createVacationRequest.execute(userId, request);

        assertThat(response).isEqualTo(expectedResponse);
        verify(vacationRequestRepository).save(any(VacationRequest.class));
    }

    private CreateVacationRequestRequestDTO validRequest() {
        return new CreateVacationRequestRequestDTO(
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(14),
                "Férias planejadas com antecedência"
        );
    }
}
