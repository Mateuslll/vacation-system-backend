package com.mateuslll.taskflow.application.usecases.vacation.create;

import com.mateuslll.taskflow.application.usecases.mappers.VacationRequestMapper;
import com.mateuslll.taskflow.application.usecases.vacation.create.dto.CreateVacationRequestRequestDTO;
import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;
import com.mateuslll.taskflow.common.exceptions.UserNotFoundException;
import com.mateuslll.taskflow.common.exceptions.VacationOverlapException;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.entities.vacation.VacationRequest;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import com.mateuslll.taskflow.domain.repository.DomainVacationRequestRepository;
import com.mateuslll.taskflow.domain.valueobject.DateRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateVacationRequest implements CreateVacationRequestUseCase {

    private final DomainVacationRequestRepository vacationRequestRepository;
    private final DomainUserRepository userRepository;
    private final VacationRequestMapper mapper;

    @Override
    @Transactional
    public VacationRequestResponseDTO execute(UUID requesterId, CreateVacationRequestRequestDTO request) {
        log.info("Criando solicitação de férias userId={}", requesterId);

        User user = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException(requesterId.toString()));

        user.ensureCanRequestVacation();
        user.ensureHasManagerAssigned();

        DateRange period = new DateRange(request.startDate(), request.endDate());

        List<VacationRequest> ownOverlaps = vacationRequestRepository.findOverlappingPendingOrApprovedForUser(
                requesterId,
                request.startDate(),
                request.endDate(),
                null
        );
        if (!ownOverlaps.isEmpty()) {
            VacationRequest conflict = ownOverlaps.getFirst();
            throw new VacationOverlapException(
                    "Já existe um pedido de férias seu (pendente ou aprovado) neste período",
                    user.getFullName(),
                    conflict.getPeriod().startDate(),
                    conflict.getPeriod().endDate(),
                    conflict.getId().toString()
            );
        }

        List<VacationRequest> crossOverlaps = vacationRequestRepository.findCrossUserOverlappingPendingOrApproved(
                request.startDate(),
                request.endDate(),
                requesterId
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

        VacationRequest vacationRequest = new VacationRequest(
                requesterId,
                period,
                request.reason()
        );

        VacationRequest saved = vacationRequestRepository.save(vacationRequest);

        log.info("Solicitação de férias criada com sucesso: {} dias ({} a {})",
            saved.getPeriod().getDays(),
            saved.getPeriod().startDate(),
            saved.getPeriod().endDate());

        return mapper.toResponse(saved, user, null);
    }
}
