package com.mateuslll.taskflow.application.usecases.mappers;

import com.mateuslll.taskflow.application.usecases.auth.dto.AuthResponse;
import com.mateuslll.taskflow.application.usecases.auth.dto.LoginRequest;
import com.mateuslll.taskflow.application.usecases.auth.dto.LoginResponse;
import com.mateuslll.taskflow.application.usecases.auth.dto.LogoutRequest;
import com.mateuslll.taskflow.application.usecases.auth.dto.RefreshTokenRequest;
import com.mateuslll.taskflow.application.usecases.auth.dto.RegisterRequest;
import com.mateuslll.taskflow.application.usecases.auth.login.LoginUserInput;
import com.mateuslll.taskflow.application.usecases.auth.login.LoginUserOutput;
import com.mateuslll.taskflow.application.usecases.auth.logout.LogoutInput;
import com.mateuslll.taskflow.application.usecases.auth.refresh.RefreshTokenInput;
import com.mateuslll.taskflow.application.usecases.auth.refresh.RefreshTokenOutput;
import com.mateuslll.taskflow.application.usecases.auth.register.RegisterUserInput;
import com.mateuslll.taskflow.application.usecases.auth.register.RegisterUserOutput;
import com.mateuslll.taskflow.common.exceptions.UnauthorizedException;
import com.mateuslll.taskflow.infrastructure.security.jwt.JwtClaimsDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthMapper {

    public LoginUserInput toLoginInput(LoginRequest request) {
        return new LoginUserInput(
                request.email(),
                request.password()
        );
    }

    public RegisterUserInput toRegisterInput(RegisterRequest request) {
        return new RegisterUserInput(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName()
        );
    }

    public RefreshTokenInput toRefreshInput(RefreshTokenRequest request) {
        return new RefreshTokenInput(request.refreshToken());
    }

    public LogoutInput toLogoutInput(String accessToken, LogoutRequest request) {
        return new LogoutInput(accessToken, request.refreshToken());
    }

    public LoginResponse toLoginResponse(LoginUserOutput output) {
        return LoginResponse.builder()
                .accessToken(output.accessToken())
                .refreshToken(output.refreshToken())
                .tokenType(output.tokenType())
                .expiresIn(output.expiresIn())
                .build();
    }

    public AuthResponse toAuthResponse(RegisterUserOutput output) {
        return AuthResponse.builder()
                .accessToken(output.accessToken())
                .refreshToken(output.refreshToken())
                .tokenType(output.tokenType())
                .expiresIn(output.expiresIn())
                .user(mapUserInfo(output.user()))
                .build();
    }

    public AuthResponse toAuthResponse(RefreshTokenOutput output) {
        return AuthResponse.builder()
                .accessToken(output.accessToken())
                .refreshToken(output.refreshToken())
                .tokenType(output.tokenType())
                .expiresIn(output.expiresIn())
                .user(mapUserInfo(output.user()))
                .build();
    }

    private AuthResponse.UserInfo mapUserInfo(RegisterUserOutput.UserInfo userInfo) {
        return AuthResponse.UserInfo.builder()
                .id(userInfo.id())
                .email(userInfo.email())
                .firstName(userInfo.firstName())
                .lastName(userInfo.lastName())
                .status(userInfo.status())
                .build();
    }

    private AuthResponse.UserInfo mapUserInfo(RefreshTokenOutput.UserInfo userInfo) {
        return AuthResponse.UserInfo.builder()
                .id(userInfo.id())
                .email(userInfo.email())
                .firstName(userInfo.firstName())
                .lastName(userInfo.lastName())
                .status(userInfo.status())
                .build();
    }

public Map<String, Object> toCurrentUserInfo(Authentication authentication) {
        Object credentials = authentication.getCredentials();
        if (!(credentials instanceof JwtClaimsDTO claims)) {
            throw new UnauthorizedException("Credenciais JWT inválidas para o usuário autenticado");
        }
        String email = claims.email() != null && !claims.email().isBlank()
                ? claims.email()
                : authentication.getName();

        return Map.of(
                "email", email,
                "userId", claims.userId(),
                "name", claims.name(),
                "roles", claims.roles(),
                "permissions", claims.rules()
        );
    }
}
