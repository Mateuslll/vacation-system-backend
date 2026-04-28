package com.mateuslll.taskflow.application.usecases.vacation.update;

import com.mateuslll.taskflow.application.usecases.mappers.VacationRequestMapper;
import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;
import com.mateuslll.taskflow.common.exceptions.ForbiddenAccessException;
import com.mateuslll.taskflow.common.exceptions.UserNotFoundException;
import com.mateuslll.taskflow.common.exceptions.VacationOverlapException;
import com.mateuslll.taskflow.common.exceptions.VacationRequestNotFoundException;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.entities.vacation.VacationRequest;
import com.mateuslll.taskflow.domain.enums.RoleName;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import com.mateuslll.taskflow.domain.repository.DomainVacationRequestRepository;
import com.mateuslll.taskflow.domain.valueobject.DateRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateVacationRequest implements UpdateVacationRequestUseCase {

    private final DomainVacationRequestRepository vacationRequestRepository;
    private final DomainUserRepository userRepository;
    private final VacationRequestMapper mapper;

    @Override
    @Transactional
    public VacationRequestResponseDTO execute(UUID vacationRequestId, UUID requesterId, UpdateVacationRequestRequestDTO request) {
        VacationRequest vacationRequest = vacationRequestRepository.findById(vacationRequestId)
                .orElseThrow(() -> new VacationRequestNotFoundException(vacationRequestId.toString()));

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException(requesterId.toString()));

        validateUpdatePermission(requester, vacationRequest);

        DateRange newPeriod = new DateRange(request.startDate(), request.endDate());
        UUID ownerId = vacationRequest.getUserId();

        List<VacationRequest> ownOverlaps = vacationRequestRepository.findOverlappingPendingOrApprovedForUser(
                ownerId,
                request.startDate(),
                request.endDate(),
                vacationRequestId
        );
        if (!ownOverlaps.isEmpty()) {
            VacationRequest conflict = ownOverlaps.getFirst();
            User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new UserNotFoundException(ownerId.toString()));
            throw new VacationOverlapException(
                    "Já existe um pedido de férias seu (pendente ou aprovado) neste período",
                    owner.getFullName(),
                    conflict.getPeriod().startDate(),
                    conflict.getPeriod().endDate(),
                    conflict.getId().toString()
            );
        }

        List<VacationRequest> crossOverlaps = vacationRequestRepository.findCrossUserOverlappingPendingOrApproved(
                request.startDate(),
                request.endDate(),
                ownerId
        );
        if (!crossOverlaps.isEmpty()) {
            VacationRequest conflict = crossOverlaps.getFirst();
            User conflictingUser = userRepository.findById(conflict.getUserId())
                    .orElseThrow(() -> new UserNotFoundException(conflict.getUserId().toString()));

            throw new VacationOverlapException(
                    "Já existe pedido de férias de outro colaborador (pendente ou aprovado) neste período",
                    conflictingUser.getFullName(),
                    conflict.getPeriod().startDate(),
                    conflict.getPeriod().endDate(),
                    conflict.getId().toString()
            );
        }

        vacationRequest.update(newPeriod, request.reason());
        VacationRequest saved = vacationRequestRepository.save(vacationRequest);

        User owner = userRepository.findById(saved.getUserId())
                .orElseThrow(() -> new UserNotFoundException(saved.getUserId().toString()));
        User approver = saved.getApprovedBy() != null
                ? userRepository.findById(saved.getApprovedBy()).orElse(null)
                : null;

        return mapper.toResponse(saved, owner, approver);
    }

    private void validateUpdatePermission(User requester, VacationRequest vacationRequest) {
        boolean isAdmin = requester.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.ADMIN);

        if (isAdmin) {
            return;
        }

        if (!vacationRequest.getUserId().equals(requester.getId())) {
            throw new ForbiddenAccessException("Você não tem permissão para atualizar esta solicitação de férias");
        }
    }
}
