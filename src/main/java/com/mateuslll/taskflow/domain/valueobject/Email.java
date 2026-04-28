package com.mateuslll.taskflow.domain.valueobject;

import com.mateuslll.taskflow.common.exceptions.DomainException;
import com.mateuslll.taskflow.common.messages.ResourceMessages;

import java.util.regex.Pattern;

public record Email(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    public Email {
        if (value == null || value.isBlank()) {
            throw new DomainException(ResourceMessages.FIELD_REQUIRED.format("Email"));
        }
        
        String trimmedEmail = value.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            throw new DomainException(ResourceMessages.EMAIL_INVALID.getMessage());
        }

        value = trimmedEmail;
    }

    @Override
    public String toString() {
        return value;
    }
}
