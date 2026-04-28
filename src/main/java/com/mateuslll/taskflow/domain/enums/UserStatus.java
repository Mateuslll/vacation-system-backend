package com.mateuslll.taskflow.domain.enums;

public enum UserStatus {
PENDING("Pendente"),

ACTIVE("Ativo"),

INACTIVE("Inativo"),

BLOCKED("Bloqueado");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canLogin() {
        return this == ACTIVE;
    }

    public boolean canBeActivated() {
        return this == PENDING || this == INACTIVE;
    }
}
