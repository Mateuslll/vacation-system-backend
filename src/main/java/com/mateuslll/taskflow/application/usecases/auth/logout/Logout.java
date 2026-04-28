package com.mateuslll.taskflow.application.usecases.auth.logout;

import com.mateuslll.taskflow.infrastructure.security.jwt.token.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Logout implements LogoutUseCase {

    private final TokenBlacklistService blacklistService;

    @Override
    public LogoutOutput execute(LogoutInput input) {
        log.info("Logout solicitado");

        String accessToken = input.accessToken();
        String refreshToken = input.refreshToken();

        if (accessToken != null && !accessToken.isBlank()) {
            try {
                blacklistService.blacklistToken(accessToken);
            } catch (Exception ignored) {
            }
        }

        if (refreshToken != null && !refreshToken.isBlank()) {
            try {
                blacklistService.blacklistToken(refreshToken);
            } catch (Exception ignored) {
            }
        }

        log.info("Logout concluído");
        return new LogoutOutput();
    }
}
