package com.mateuslll.taskflow.domain.enums;

public enum RoleName {
USER("Usuário", "ROLE_USER"),

MANAGER("Gerente", "ROLE_MANAGER"),

ADMIN("Administrador", "ROLE_ADMIN");

    private final String description;
    private final String authority;

    RoleName(String description, String authority) {
        this.description = description;
        this.authority = authority;
    }

    public String getDescription() {
        return description;
    }

public String getAuthority() {
        return authority;
    }
}
