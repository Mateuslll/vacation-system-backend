package com.mateuslll.taskflow.application.usecases.user.assignrole;

import com.mateuslll.taskflow.application.usecases.mappers.UserMapper;
import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;
import com.mateuslll.taskflow.common.exceptions.BadRequestException;
import com.mateuslll.taskflow.common.exceptions.RoleNotFoundException;
import com.mateuslll.taskflow.common.exceptions.UserNotFoundException;
import com.mateuslll.taskflow.domain.entities.user.Role;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.enums.RoleName;
import com.mateuslll.taskflow.domain.repository.DomainRoleRepository;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignRole implements AssignRoleUseCase {

    private final DomainUserRepository userRepository;
    private final DomainRoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDTO execute(UUID userId, AssignRoleRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        RoleName roleName;
        try {
            roleName = RoleName.valueOf(request.roleName().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Role inválida: " + request.roleName() + 
                    ". Valores válidos: USER, MANAGER, ADMIN"
            );
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName.name()));

        boolean alreadyHasRole = user.getRoles().stream()
                .anyMatch(r -> r.getName() == roleName);

        if (alreadyHasRole) {
            throw new BadRequestException(
                    "Usuário " + user.getFullName() + " já possui a role " + roleName.name()
            );
        }

        user.addRole(role);

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}
