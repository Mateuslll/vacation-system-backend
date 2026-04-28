package com.mateuslll.taskflow.domain.enums;

public enum RequestStatus {
PENDING("Pendente"),

APPROVED("Aprovada"),

REJECTED("Rejeitada"),

CANCELLED("Cancelada");

    private final String description;

    RequestStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isFinal() {
        return this == APPROVED || this == REJECTED || this == CANCELLED;
    }

    public boolean canBeModified() {
        return this == PENDING;
    }
}
