package com.mateuslll.taskflow.application.usecases.bootstrap;

import com.mateuslll.taskflow.common.exceptions.EmailAlreadyRegisteredException;
import com.mateuslll.taskflow.common.exceptions.RoleNotFoundException;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.enums.RoleName;
import com.mateuslll.taskflow.domain.repository.DomainRoleRepository;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import com.mateuslll.taskflow.domain.valueobject.Email;
import com.mateuslll.taskflow.domain.valueobject.Password;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateAdmin {

    private final DomainUserRepository userRepository;
    private final DomainRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CreateAdminOutput execute(CreateAdminInput input) {
        log.info("Bootstrap: criar administrador email={}", input.email());

        Email email = new Email(input.email());
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(input.email());
        }

        String hashedPassword = passwordEncoder.encode(input.password());
        Password password = new Password(hashedPassword);

        var adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new RoleNotFoundException("ADMIN"));

        User user = new User(email, password, input.firstName(), input.lastName());
        user.addRole(adminRole);

        User savedUser = userRepository.save(user);

        log.info("Administrador criado id={}", savedUser.getId());

        return new CreateAdminOutput(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail().value(),
                "Administrador criado com sucesso"
        );
    }
}
