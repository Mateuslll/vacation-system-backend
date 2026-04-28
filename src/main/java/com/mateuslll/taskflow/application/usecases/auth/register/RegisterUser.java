package com.mateuslll.taskflow.application.usecases.auth.register;

import com.mateuslll.taskflow.application.usecases.auth.shared.AuthClaimsFactory;
import com.mateuslll.taskflow.common.exceptions.EmailAlreadyRegisteredException;
import com.mateuslll.taskflow.common.exceptions.RoleNotFoundException;
import com.mateuslll.taskflow.domain.entities.user.Role;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.enums.RoleName;
import com.mateuslll.taskflow.domain.repository.DomainRoleRepository;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import com.mateuslll.taskflow.domain.valueobject.Email;
import com.mateuslll.taskflow.domain.valueobject.Password;
import com.mateuslll.taskflow.infrastructure.security.jwt.JwtProperties;
import com.mateuslll.taskflow.infrastructure.security.jwt.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUser implements RegisterUserUseCase {

    private final DomainUserRepository userRepository;
    private final DomainRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final JwtProperties jwtProperties;
    private final AuthClaimsFactory authClaimsFactory;

    @Override
    @Transactional
    public RegisterUserOutput execute(RegisterUserInput input) {
        log.info("Registro de usuário solicitado email={}", input.email());

        Email email = new Email(input.email());
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(input.email());
        }

        Password password = new Password(passwordEncoder.encode(input.password()));
        Role userRole = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new RoleNotFoundException("USER"));
        User user = new User(email, password, input.firstName(), input.lastName());
        user.addRole(userRole);

        User savedUser = userRepository.save(user);
        log.info("Registro concluído id={}", savedUser.getId());

        Map<String, Object> claims = authClaimsFactory.build(savedUser);
        String accessToken = tokenService.generateAccessToken(claims);
        String refreshToken = tokenService.generateRefreshToken(claims);

        return RegisterUserOutput.of(
                accessToken,
                refreshToken,
                jwtProperties.getAccessTokenExpirationInSeconds(),
                buildUserInfo(savedUser)
        );
    }

private RegisterUserOutput.UserInfo buildUserInfo(User user) {
        return RegisterUserOutput.UserInfo.builder()
                .id(user.getId().toString())
                .email(user.getEmail().value())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus().name())
                .build();
    }
}
