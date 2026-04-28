package com.mateuslll.taskflow.application.usecases.user.collaborators;

import com.mateuslll.taskflow.application.usecases.mappers.UserMapper;
import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;
import com.mateuslll.taskflow.common.exceptions.UserNotFoundException;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetCollaboratorsByManager implements GetCollaboratorsByManagerUseCase {

    private final DomainUserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> execute(GetCollaboratorsByManagerRequestDTO request) {
       userRepository.findById(request.managerId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Manager não encontrado: " + request.managerId()));

       List<User> collaborators = userRepository.findByManagerId(request.managerId());

        return collaborators.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }
}
