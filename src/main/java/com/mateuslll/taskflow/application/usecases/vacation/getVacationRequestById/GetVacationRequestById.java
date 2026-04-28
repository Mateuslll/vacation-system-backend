package com.mateuslll.taskflow.application.usecases.vacation.getVacationRequestById;

import com.mateuslll.taskflow.application.usecases.mappers.VacationRequestMapper;
import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;
import com.mateuslll.taskflow.common.exceptions.ForbiddenAccessException;
import com.mateuslll.taskflow.common.exceptions.UserNotFoundException;
import com.mateuslll.taskflow.common.exceptions.VacationRequestNotFoundException;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.entities.vacation.VacationRequest;
import com.mateuslll.taskflow.domain.enums.RoleName;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import com.mateuslll.taskflow.domain.repository.DomainVacationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetVacationRequestById implements GetVacationRequestByIdUseCase {

    private final DomainVacationRequestRepository vacationRequestRepository;
    private final DomainUserRepository userRepository;
    private final VacationRequestMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public VacationRequestResponseDTO execute(GetVacationRequestByIdRequestDTO request) {
        log.info("Buscando solicitação de férias id={}", request.vacationRequestId());

        VacationRequest vacation = vacationRequestRepository.findById(request.vacationRequestId())
                .orElseThrow(() -> new VacationRequestNotFoundException(request.vacationRequestId().toString()));

        Set<UUID> userIds = new HashSet<>();
        userIds.add(request.requesterId());
        userIds.add(vacation.getUserId());
        if (vacation.getApprovedBy() != null) {
            userIds.add(vacation.getApprovedBy());
        }

        Map<UUID, User> usersById = userRepository.findAllByIdIn(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));

        User requester = Optional.ofNullable(usersById.get(request.requesterId()))
                .orElseThrow(() -> new UserNotFoundException(request.requesterId().toString()));
        User owner = Optional.ofNullable(usersById.get(vacation.getUserId()))
                .orElseThrow(() -> new UserNotFoundException(vacation.getUserId().toString()));
        User approver = vacation.getApprovedBy() != null ? usersById.get(vacation.getApprovedBy()) : null;

        ensureRequesterMayView(requester, vacation);

        log.info("Solicitação de férias encontrada id={}", vacation.getId());
        return mapper.toResponse(vacation, owner, approver);
    }

    private void ensureRequesterMayView(User requester, VacationRequest vacation) {
        UUID requesterId = requester.getId();
        UUID ownerId = vacation.getUserId();

        if (hasRole(requester, RoleName.ADMIN) || requesterId.equals(ownerId)) {
            return;
        }
        if (hasRole(requester, RoleName.MANAGER)
                && userRepository.existsByIdAndManagerId(ownerId, requesterId)) {
            return;
        }
        throw new ForbiddenAccessException("Você não tem permissão para visualizar esta solicitação de férias");
    }

    private static boolean hasRole(User user, RoleName roleName) {
        return user.getRoles().stream().anyMatch(r -> r.getName() == roleName);
    }
}
