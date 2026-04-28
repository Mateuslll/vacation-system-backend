package com.mateuslll.taskflow.application.usecases.user.delete;

import com.mateuslll.taskflow.common.exceptions.BadRequestException;
import com.mateuslll.taskflow.common.exceptions.UserNotFoundException;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import com.mateuslll.taskflow.domain.repository.DomainVacationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteUser implements DeleteUserUseCase {

    private final DomainUserRepository userRepository;
    private final DomainVacationRequestRepository vacationRequestRepository;

    @Override
    @Transactional
    public void execute(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        if (!vacationRequestRepository.findByUserId(userId).isEmpty()) {
            throw new BadRequestException("Usuário possui solicitações de férias e não pode ser removido");
        }

        userRepository.deleteById(userId);
    }
}
