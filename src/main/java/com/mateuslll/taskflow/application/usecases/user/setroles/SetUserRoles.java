package com.mateuslll.taskflow.application.usecases.user.setroles;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SetUserRoles implements SetUserRolesUseCase {

    private final DomainUserRepository userRepository;
    private final DomainRoleRepository roleRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDTO execute(UUID userId, SetRolesRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        List<String> requestedRoles = request.roles();
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            throw new BadRequestException("roles é obrigatório e não pode estar vazio");
        }

        Set<RoleName> roleNames = new HashSet<>();
        for (String roleStr : requestedRoles) {
            if (roleStr == null || roleStr.isBlank()) {
                continue;
            }
            try {
                roleNames.add(RoleName.valueOf(roleStr.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException(
                        "Role inválida: " + roleStr + ". Valores válidos: USER, MANAGER, ADMIN"
                );
            }
        }

        if (roleNames.isEmpty()) {
            throw new BadRequestException("roles é obrigatório e não pode estar vazio");
        }

        Set<Role> desiredRoles = roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new RoleNotFoundException(name.name())))
                .collect(java.util.stream.Collectors.toSet());

        for (Role existing : Set.copyOf(user.getRoles())) {
            boolean keep = desiredRoles.stream().anyMatch(r -> r.getName() == existing.getName());
            if (!keep) {
                user.removeRole(existing);
            }
        }

        for (Role desired : desiredRoles) {
            boolean alreadyHas = user.getRoles().stream().anyMatch(r -> r.getName() == desired.getName());
            if (!alreadyHas) {
                user.addRole(desired);
            }
        }

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }
}

