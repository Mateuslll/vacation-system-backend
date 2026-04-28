package com.mateuslll.taskflow.application.usecases.auth.login;

import com.mateuslll.taskflow.application.usecases.auth.shared.AuthClaimsFactory;
import com.mateuslll.taskflow.common.exceptions.InvalidCredentialsException;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import com.mateuslll.taskflow.domain.valueobject.Email;
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
public class LoginUser implements LoginUserUseCase {

    private final DomainUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final JwtProperties jwtProperties;
    private final AuthClaimsFactory authClaimsFactory;

    @Override
    @Transactional
    public LoginUserOutput execute(LoginUserInput input) {
        log.info("Login solicitado email={}", input.email());

        Email email = new Email(input.email());
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(input.password(), user.getPassword().hashedValue())) {
            throw new InvalidCredentialsException();
        }

        if (!user.isActive()) {
            throw new InvalidCredentialsException("Usuário inativo ou bloqueado");
        }

        user.registerLogin();
        userRepository.save(user);

        Map<String, Object> claims = authClaimsFactory.build(user);
        String accessToken = tokenService.generateAccessToken(claims);
        String refreshToken = tokenService.generateRefreshToken(claims);

        log.info("Login concluído id={}", user.getId());

        return LoginUserOutput.of(
                accessToken,
                refreshToken,
                jwtProperties.getAccessTokenExpirationInSeconds(),
                buildUserInfo(user)
        );
    }

private LoginUserOutput.UserInfo buildUserInfo(User user) {
        return LoginUserOutput.UserInfo.builder()
                .id(user.getId().toString())
                .email(user.getEmail().value())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus().name())
                .build();
    }
}
