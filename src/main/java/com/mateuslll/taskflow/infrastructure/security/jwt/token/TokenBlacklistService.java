package com.mateuslll.taskflow.infrastructure.security.jwt.token;

import com.mateuslll.taskflow.infrastructure.security.jwt.JwtClaims;
import com.mateuslll.taskflow.infrastructure.security.jwt.token.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenService tokenService;
    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();

    private static final String BLACKLIST_PREFIX = "blacklist:token:";

    public boolean blacklistToken(String token) {
        try {
            Optional<JwtClaims> claimsOpt = tokenService.extractClaims(token);
            
            if (claimsOpt.isEmpty()) {
                return false;
            }

            JwtClaims jwtClaims = claimsOpt.get();
            String tokenId = jwtClaims.get("jti", String.class);
            Instant expiration = jwtClaims.getExpiration();

            if (tokenId == null) {
                return false;
            }

            long ttlSeconds = calculateTTL(expiration);
            
            if (ttlSeconds <= 0) {
                return true;
            }

            String key = BLACKLIST_PREFIX + tokenId;
            blacklist.put(key, expiration);
            
            log.info("Token adicionado à blacklist. ID: {}, TTL: {}s", tokenId, ttlSeconds);
            return true;
            
        } catch (Exception e) {
            log.error("Erro ao adicionar token à blacklist: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean isTokenBlacklisted(String token) {
        try {
            Optional<JwtClaims> claimsOpt = tokenService.extractClaims(token);
            
            if (claimsOpt.isEmpty()) {
                return false;
            }

            JwtClaims jwtClaims = claimsOpt.get();
            String tokenId = jwtClaims.get("jti", String.class);

            if (tokenId == null) {
                return false;
            }

            String key = BLACKLIST_PREFIX + tokenId;
            Instant expiration = blacklist.get(key);
            if (expiration == null) {
                return false;
            }
            if (expiration.isBefore(Instant.now())) {
                blacklist.remove(key);
                return false;
            }
            return true;
            
        } catch (Exception e) {
            log.error("Erro ao verificar blacklist: {}", e.getMessage(), e);
            return false;
        }
    }

    private long calculateTTL(Instant expiration) {
        if (expiration == null) {
            return 3600;
        }

        Instant now = Instant.now();
        
        long ttlSeconds = Duration.between(now, expiration).getSeconds();
        return Math.max(ttlSeconds, 0);
    }
}
