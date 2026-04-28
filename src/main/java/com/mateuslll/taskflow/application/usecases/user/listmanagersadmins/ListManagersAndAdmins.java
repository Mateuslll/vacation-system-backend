package com.mateuslll.taskflow.application.usecases.user.listmanagersadmins;

import com.mateuslll.taskflow.application.usecases.mappers.UserMapper;
import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.enums.RoleName;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListManagersAndAdmins implements ListManagersAndAdminsUseCase {

    private final DomainUserRepository userRepository;
    private final UserMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> execute(Void input) {
        log.info("Listando MANAGER e ADMIN");

        List<User> allUsers = userRepository.findAll();

        List<UserResponseDTO> managersAndAdmins = allUsers.stream()
            .filter(User::isActive)
            .filter(this::hasManagerOrAdminRole)
            .sorted(Comparator.comparing(User::getFullName))
            .map(mapper::toResponse)
            .collect(Collectors.toList());

        log.info("MANAGER/ADMIN retornados total={}", managersAndAdmins.size());

        return managersAndAdmins;
    }

private boolean hasManagerOrAdminRole(User user) {
        return user.getRoles().stream()
            .anyMatch(role -> 
                role.getName() == RoleName.MANAGER || 
                role.getName() == RoleName.ADMIN
            );
    }
}
