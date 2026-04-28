package com.mateuslll.taskflow.application.usecases.user.assignmanager;

import com.mateuslll.taskflow.application.usecases.mappers.UserMapper;
import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;
import com.mateuslll.taskflow.common.exceptions.BadRequestException;
import com.mateuslll.taskflow.common.exceptions.UserNotFoundException;
import com.mateuslll.taskflow.domain.entities.user.Role;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.enums.RoleName;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssignManager implements AssignManagerUseCase {

    private final DomainUserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDTO execute(AssignManagerRequestDTO request) {
        User collaborator = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId().toString()));

        User manager = userRepository.findById(request.managerId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Manager não encontrado: " + request.managerId()
                ));

        boolean isManagerRole = manager.getRoles().stream()
                .map(Role::getName)
                .anyMatch(roleName -> roleName == RoleName.MANAGER || roleName == RoleName.ADMIN);

        if (!isManagerRole) {
            throw new BadRequestException(
                    "Usuário " + manager.getFullName() + " não possui role MANAGER ou ADMIN"
            );
        }

        collaborator.assignManager(request.managerId());

        User savedCollaborator = userRepository.save(collaborator);

        return userMapper.toResponse(savedCollaborator);
    }
}
