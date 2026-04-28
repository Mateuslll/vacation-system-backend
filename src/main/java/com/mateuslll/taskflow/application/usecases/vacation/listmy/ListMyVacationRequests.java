package com.mateuslll.taskflow.application.usecases.vacation.listmy;

import com.mateuslll.taskflow.application.usecases.mappers.VacationRequestMapper;
import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.entities.vacation.VacationRequest;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import com.mateuslll.taskflow.domain.repository.DomainVacationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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
public class ListMyVacationRequests implements ListMyVacationRequestsUseCase {

    private final DomainVacationRequestRepository vacationRequestRepository;
    private final DomainUserRepository userRepository;
    private final VacationRequestMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<VacationRequestResponseDTO> execute(UUID userId) {
        log.info("Listando minhas solicitações de férias userId={}", userId);

        List<VacationRequest> vacationRequests = vacationRequestRepository.findByUserId(userId);

        log.info("Minhas solicitações retornadas total={}", vacationRequests.size());

        User owner = userRepository.findById(userId).orElse(null);

        Set<UUID> approverIds = new HashSet<>();
        for (VacationRequest vr : vacationRequests) {
            if (vr.getApprovedBy() != null) {
                approverIds.add(vr.getApprovedBy());
            }
        }

        Map<UUID, User> approversById = userRepository.findAllByIdIn(approverIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));

        return vacationRequests.stream()
                .sorted(Comparator.comparing(VacationRequest::getCreatedAt).reversed())
                .map(vr -> mapper.toResponse(
                        vr,
                        owner,
                        vr.getApprovedBy() != null ? approversById.get(vr.getApprovedBy()) : null
                ))
                .collect(Collectors.toList());
    }
}
