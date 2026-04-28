package com.mateuslll.taskflow.application.usecases.vacation.reject;

import com.mateuslll.taskflow.application.usecases.mappers.VacationRequestMapper;
import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;
import com.mateuslll.taskflow.common.exceptions.ForbiddenAccessException;
import com.mateuslll.taskflow.common.exceptions.UserNotFoundException;
import com.mateuslll.taskflow.common.exceptions.VacationRequestNotFoundException;
import com.mateuslll.taskflow.domain.entities.user.Role;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.entities.vacation.VacationRequest;
import com.mateuslll.taskflow.domain.enums.RoleName;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import com.mateuslll.taskflow.domain.repository.DomainVacationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RejectVacationRequest implements RejectVacationRequestUseCase {

    private final DomainVacationRequestRepository vacationRequestRepository;
    private final DomainUserRepository userRepository;
    private final VacationRequestMapper mapper;

    @Override
    @Transactional
    public VacationRequestResponseDTO execute(UUID vacationRequestId, UUID managerId, String rejectionReason) {
        VacationRequest vacationRequest = vacationRequestRepository.findById(vacationRequestId)
                .orElseThrow(() -> new VacationRequestNotFoundException(vacationRequestId.toString()));

        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new UserNotFoundException(managerId.toString()));

        User collaborator = userRepository.findById(vacationRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException(vacationRequest.getUserId().toString()));

        validateManagerHierarchy(manager, collaborator);

        vacationRequest.reject(managerId, rejectionReason);

        VacationRequest saved = vacationRequestRepository.save(vacationRequest);

        return mapper.toResponse(saved, collaborator, manager);
    }

    private void validateManagerHierarchy(User manager, User collaborator) {
        boolean isAdmin = manager.getRoles().stream()
                .map(Role::getName)
                .anyMatch(roleName -> roleName == RoleName.ADMIN);

        if (isAdmin) {
            return;
        }

        if (!collaborator.isManagedBy(manager.getId())) {
            throw new ForbiddenAccessException(
                    "Manager " + manager.getFullName() + " não pode rejeitar férias de " +
                    collaborator.getFullName() + " (não é seu colaborador)"
            );
        }
    }
}
