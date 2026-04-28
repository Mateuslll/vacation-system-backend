package com.mateuslll.taskflow.application.usecases.auth.shared;

import com.mateuslll.taskflow.domain.entities.user.User;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AuthClaimsFactory {

    public Map<String, Object> build(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("email", user.getEmail().value());
        claims.put("name", user.getFirstName() + " " + user.getLastName());
        claims.put("roles", extractRoles(user));
        claims.put("rules", extractPermissions(user));
        claims.put("status", user.getStatus().name());
        return claims;
    }

    private List<String> extractRoles(User user) {
        return user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();
    }

    private List<String> extractPermissions(User user) {
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .distinct()
                .toList();
    }
}
