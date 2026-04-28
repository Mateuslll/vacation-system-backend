package com.mateuslll.taskflow.application.usecases.vacation.cancel;

import com.mateuslll.taskflow.application.usecases.mappers.VacationRequestMapper;
import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;
import com.mateuslll.taskflow.common.exceptions.UserNotFoundException;
import com.mateuslll.taskflow.common.exceptions.VacationRequestNotFoundException;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.entities.vacation.VacationRequest;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import com.mateuslll.taskflow.domain.repository.DomainVacationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CancelVacationRequest implements CancelVacationRequestUseCase {

    private final DomainVacationRequestRepository vacationRequestRepository;
    private final DomainUserRepository userRepository;
    private final VacationRequestMapper mapper;

    @Override
    @Transactional
    public VacationRequestResponseDTO execute(UUID vacationRequestId, UUID requesterId) {
        VacationRequest vacationRequest = vacationRequestRepository.findById(vacationRequestId)
                .orElseThrow(() -> new VacationRequestNotFoundException(vacationRequestId.toString()));

        User user = userRepository.findById(requesterId)
                .orElseThrow(() -> new UserNotFoundException(requesterId.toString()));

        vacationRequest.cancel(requesterId);

        VacationRequest saved = vacationRequestRepository.save(vacationRequest);

        return mapper.toResponse(saved, user, null);
    }
}
