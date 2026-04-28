package com.mateuslll.taskflow.application.usecases.vacation.getapproved;

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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetApprovedVacations implements GetApprovedVacationsUseCase {

    private final DomainVacationRequestRepository vacationRequestRepository;
    private final DomainUserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ApprovedVacationPeriodDTO> execute(GetApprovedVacationsRequestDTO input) {
        log.info("Buscando férias aprovadas (filtro startDate={}, endDate={})",
                input.startDate(), input.endDate());

        List<VacationRequest> approvedVacations = vacationRequestRepository.findByStatus(RequestStatus.APPROVED);

        List<VacationRequest> filtered = approvedVacations.stream()
                .filter(vacation -> matchesDateFilter(vacation, input.startDate(), input.endDate()))
                .sorted(Comparator.comparing(vr -> vr.getPeriod().startDate()))
                .toList();

        Set<UUID> userIds = filtered.stream()
                .map(VacationRequest::getUserId)
                .collect(Collectors.toSet());

        Map<UUID, User> usersById = userRepository.findAllByIdIn(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a));

        List<ApprovedVacationPeriodDTO> result = filtered.stream()
                .map(v -> toDTO(v, usersById.get(v.getUserId())))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("Férias aprovadas retornadas: total={}", result.size());
        return result;
    }

    private boolean matchesDateFilter(VacationRequest vacation, LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return true;
        }

        LocalDate vacationStart = vacation.getPeriod().startDate();
        LocalDate vacationEnd = vacation.getPeriod().endDate();

        if (startDate != null && endDate != null) {
            return !vacationStart.isAfter(endDate) && !vacationEnd.isBefore(startDate);
        }

        if (startDate != null) {
            return !vacationEnd.isBefore(startDate);
        }

        return !vacationStart.isAfter(endDate);
    }

    private ApprovedVacationPeriodDTO toDTO(VacationRequest vacation, User user) {
        if (user == null) {
            return null;
        }

        return ApprovedVacationPeriodDTO.of(
                vacation.getId(),
                user.getId(),
                user.getFullName(),
                user.getEmail().value(),
                vacation.getPeriod().startDate(),
                vacation.getPeriod().endDate(),
                (int) vacation.getPeriod().getDays(),
                vacation.getStatus().name()
        );
    }
}
