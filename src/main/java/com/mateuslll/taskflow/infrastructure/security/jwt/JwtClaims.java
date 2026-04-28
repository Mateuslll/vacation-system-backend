package com.mateuslll.taskflow.infrastructure.security.jwt;

import io.jsonwebtoken.Claims;
import lombok.Builder;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Builder
public record JwtClaims(
        Claims claims
) {
public <T> T get(String key, Class<T> type) {
        return claims.get(key, type);
    }

public String getSubject() {
        return claims.getSubject();
    }

public String getUserId() {
        return claims.get("userId", String.class);
    }

public String getEmail() {
        return claims.get("email", String.class);
    }

public String getName() {
        return claims.get("name", String.class);
    }

@SuppressWarnings("unchecked")
    public List<String> getRoles() {
        return claims.get("roles", List.class);
    }

@SuppressWarnings("unchecked")
    public List<String> getRules() {
        return claims.get("rules", List.class);
    }

public Instant getIssuedAt() {
        Date issuedAt = claims.getIssuedAt();
        return issuedAt != null ? issuedAt.toInstant() : null;
    }

public Instant getExpiration() {
        Date expiration = claims.getExpiration();
        return expiration != null ? expiration.toInstant() : null;
    }

public Map<String, Object> getAllClaims() {
        return claims;
    }

public boolean isExpired() {
        return claims.getExpiration().before(new Date());
    }
}
