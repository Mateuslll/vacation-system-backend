package com.mateuslll.taskflow.infrastructure.security.jwt;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record JwtClaimsDTO(
        String userId,
        String email,
        String name,
        List<String> roles,
        List<String> rules,
        Map<String, Object> additionalClaims
) {
public Map<String, Object> toMap() {
        Map<String, Object> map = new java.util.HashMap<>(additionalClaims != null ? additionalClaims : Map.of());
        map.put("userId", userId);
        map.put("email", email);
        map.put("name", name);
        map.put("roles", roles);
        map.put("rules", rules);
        return map;
    }
}
