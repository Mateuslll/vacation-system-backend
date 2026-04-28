package com.mateuslll.taskflow.application.usecases.user.getall;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatusFilter {

    ACTIVE("ACTIVE"),

    INACTIVE("INACTIVE"),

    ALL("ALL");

    private final String value;

    UserStatusFilter(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static UserStatusFilter fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        String normalized = value.trim().toUpperCase();
        for (UserStatusFilter filter : UserStatusFilter.values()) {
            if (filter.value.equals(normalized)) {
                return filter;
            }
        }
        
        throw new IllegalArgumentException(
            "Status filter inválido: '" + value + "'. " +
            "Valores aceitos: ACTIVE, INACTIVE, ALL"
        );
    }

    @Override
    public String toString() {
        return value;
    }
}
