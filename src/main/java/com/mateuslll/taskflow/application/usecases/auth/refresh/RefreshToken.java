package com.mateuslll.taskflow.application.usecases.auth.refresh;

import com.mateuslll.taskflow.application.usecases.auth.shared.AuthClaimsFactory;
import com.mateuslll.taskflow.common.exceptions.InvalidTokenException;
import com.mateuslll.taskflow.common.exceptions.UserNotFoundException;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.repository.DomainUserRepository;
import com.mateuslll.taskflow.domain.valueobject.Email;
import com.mateuslll.taskflow.infrastructure.security.jwt.JwtClaims;
import com.mateuslll.taskflow.infrastructure.security.jwt.JwtProperties;
import com.mateuslll.taskflow.infrastructure.security.jwt.token.service.TokenService;
import com.mateuslll.taskflow.infrastructure.security.jwt.token.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshToken implements RefreshTokenUseCase {

    private final DomainUserRepository userRepository;
    private final TokenService tokenService;
    private final JwtProperties jwtProperties;
    private final AuthClaimsFactory authClaimsFactory;

    @Override
    @Transactional(readOnly = true)
    public RefreshTokenOutput execute(RefreshTokenInput input) {
        log.info("Refresh de token solicitado");

        if (!tokenService.isTokenValid(input.refreshToken(), TokenType.REFRESH)) {
            throw new InvalidTokenException("Refresh token inválido ou expirado");
        }

        JwtClaims claims = tokenService.extractClaims(input.refreshToken())
                .orElseThrow(() -> new InvalidTokenException("Não foi possível extrair claims do refresh token"));

        String userEmail = claims.getEmail();

        Email email = new Email(userEmail);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(userEmail));

        if (!user.isActive()) {
            throw new InvalidTokenException("Usuário inativo ou bloqueado");
        }

        Map<String, Object> newClaims = authClaimsFactory.build(user);
        String newAccessToken = tokenService.generateAccessToken(newClaims);
        String newRefreshToken = tokenService.generateRefreshToken(newClaims);

        log.info("Refresh concluído id={}", user.getId());

        return RefreshTokenOutput.of(
                newAccessToken,
                newRefreshToken,
                jwtProperties.getAccessTokenExpirationInSeconds(),
                buildUserInfo(user)
        );
    }

private RefreshTokenOutput.UserInfo buildUserInfo(User user) {
        return RefreshTokenOutput.UserInfo.builder()
                .id(user.getId().toString())
                .email(user.getEmail().value())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus().name())
                .build();
    }
}
