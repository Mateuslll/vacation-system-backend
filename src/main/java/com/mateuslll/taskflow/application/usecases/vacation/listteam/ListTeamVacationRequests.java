package com.mateuslll.taskflow.application.usecases.vacation.listteam;

import com.mateuslll.taskflow.application.usecases.mappers.VacationRequestMapper;
import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;
import com.mateuslll.taskflow.common.exceptions.UserNotFoundException;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListTeamVacationRequests implements ListTeamVacationRequestsUseCase {

    private final DomainVacationRequestRepository vacationRequestRepository;
    private final DomainUserRepository userRepository;
    private final VacationRequestMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<VacationRequestResponseDTO> execute(UUID requesterId) {
        log.info("Listando férias da equipe requesterId={}", requesterId);

        User requester = userRepository.findById(requesterId)
            .orElseThrow(() -> new UserNotFoundException(requesterId.toString()));

        boolean isAdmin = requester.getRoles().stream()
            .anyMatch(role -> role.getName() == RoleName.ADMIN);

        List<VacationRequest> vacationRequests;

        if (isAdmin) {
            vacationRequests = vacationRequestRepository.findByFilters(null, null, null, null);
        } else {
            List<User> collaborators = userRepository.findByManagerId(requesterId);

            List<UUID> collaboratorIds = collaborators.stream()
                .map(User::getId)
                .collect(Collectors.toList());

            vacationRequests = vacationRequestRepository.findByUserIds(collaboratorIds);
        }

        log.info("Férias da equipe retornadas total={}", vacationRequests.size());

        Set<UUID> userIds = new HashSet<>();
        for (VacationRequest vr : vacationRequests) {
            userIds.add(vr.getUserId());
            if (vr.getApprovedBy() != null) {
                userIds.add(vr.getApprovedBy());
            }
        }

        Map<UUID, User> usersById = userRepository.findAllByIdIn(userIds).stream()
            .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));

        return vacationRequests.stream()
            .map(vr -> mapper.toResponse(
                vr,
                usersById.get(vr.getUserId()),
                vr.getApprovedBy() != null ? usersById.get(vr.getApprovedBy()) : null
            ))
            .collect(Collectors.toList());
    }
}
