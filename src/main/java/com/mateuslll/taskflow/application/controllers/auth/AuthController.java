package com.mateuslll.taskflow.application.controllers.auth;

import com.mateuslll.taskflow.application.controllers.docs.AuthAPI;
import com.mateuslll.taskflow.application.usecases.auth.dto.AuthResponse;
import com.mateuslll.taskflow.application.usecases.auth.dto.LoginRequest;
import com.mateuslll.taskflow.application.usecases.auth.dto.LoginResponse;
import com.mateuslll.taskflow.application.usecases.auth.dto.LogoutRequest;
import com.mateuslll.taskflow.application.usecases.auth.dto.RefreshTokenRequest;
import com.mateuslll.taskflow.application.usecases.auth.dto.RegisterRequest;
import com.mateuslll.taskflow.application.usecases.auth.login.LoginUserInput;
import com.mateuslll.taskflow.application.usecases.auth.login.LoginUserOutput;
import com.mateuslll.taskflow.application.usecases.auth.login.LoginUserUseCase;
import com.mateuslll.taskflow.application.usecases.auth.logout.LogoutInput;
import com.mateuslll.taskflow.application.usecases.auth.logout.LogoutUseCase;
import com.mateuslll.taskflow.application.usecases.auth.refresh.RefreshTokenInput;
import com.mateuslll.taskflow.application.usecases.auth.refresh.RefreshTokenOutput;
import com.mateuslll.taskflow.application.usecases.auth.refresh.RefreshTokenUseCase;
import com.mateuslll.taskflow.application.usecases.auth.register.RegisterUserInput;
import com.mateuslll.taskflow.application.usecases.auth.register.RegisterUserOutput;
import com.mateuslll.taskflow.application.usecases.auth.register.RegisterUserUseCase;
import com.mateuslll.taskflow.application.usecases.mappers.AuthMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints de autenticação e autorização")
public class AuthController implements AuthAPI {

    private final LoginUserUseCase loginUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final AuthMapper authMapper;

    @Override
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginUserInput input = authMapper.toLoginInput(request);
        LoginUserOutput output = loginUserUseCase.execute(input);
        LoginResponse response = authMapper.toLoginResponse(output);

        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterUserInput input = authMapper.toRegisterInput(request);
        RegisterUserOutput output = registerUserUseCase.execute(input);
        AuthResponse response = authMapper.toAuthResponse(output);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenInput input = authMapper.toRefreshInput(request);
        RefreshTokenOutput output = refreshTokenUseCase.execute(input);
        AuthResponse response = authMapper.toAuthResponse(output);

        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> userInfo = authMapper.toCurrentUserInfo(authentication);
        return ResponseEntity.ok(userInfo);
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody LogoutRequest request) {
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }

        LogoutInput input = authMapper.toLogoutInput(accessToken, request);
        logoutUseCase.execute(input);
        SecurityContextHolder.clearContext();

        return ResponseEntity.noContent().build();
    }
}
