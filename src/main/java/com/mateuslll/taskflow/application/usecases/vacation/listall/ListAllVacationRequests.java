package com.mateuslll.taskflow.application.usecases.vacation.listall;

import com.mateuslll.taskflow.application.usecases.mappers.VacationRequestMapper;
import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.entities.vacation.VacationRequest;
import com.mateuslll.taskflow.domain.enums.RequestStatus;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import com.mateuslll.taskflow.domain.repository.DomainVacationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
public class ListAllVacationRequests implements ListAllVacationRequestsUseCase {

    private final DomainVacationRequestRepository vacationRequestRepository;
    private final DomainUserRepository userRepository;
    private final VacationRequestMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<VacationRequestResponseDTO> execute(
        RequestStatus status,
        UUID userId,
        LocalDate startDate,
        LocalDate endDate
    ) {
        log.info("Listando solicitações de férias (admin/manager)");

        List<VacationRequest> filteredRequests = vacationRequestRepository.findByFilters(
                status, userId, startDate, endDate
        );

        log.info("Solicitações de férias retornadas total={}", filteredRequests.size());

        Set<UUID> userIds = new HashSet<>();
        for (VacationRequest vr : filteredRequests) {
            userIds.add(vr.getUserId());
            if (vr.getApprovedBy() != null) {
                userIds.add(vr.getApprovedBy());
            }
        }

        Map<UUID, User> usersById = userRepository.findAllByIdIn(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));

        return filteredRequests.stream()
            .map(vr -> mapper.toResponse(
                vr,
                usersById.get(vr.getUserId()),
                vr.getApprovedBy() != null ? usersById.get(vr.getApprovedBy()) : null
            ))
            .collect(Collectors.toList());
    }
}
