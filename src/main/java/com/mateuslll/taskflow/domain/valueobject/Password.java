package com.mateuslll.taskflow.domain.valueobject;

import com.mateuslll.taskflow.common.exceptions.InvalidPasswordException;
import com.mateuslll.taskflow.common.messages.ResourceMessages;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Pattern;

public record Password(String hashedValue) {

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"
    );

public static Password fromPlainText(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new InvalidPasswordException(ResourceMessages.PASSWORD_CANNOT_BE_EMPTY.getMessage());
        }

        if (!PASSWORD_PATTERN.matcher(plainPassword).matches()) {
            throw new InvalidPasswordException(ResourceMessages.PASSWORD_REQUIREMENTS.getMessage());
        }

        String hashed = PASSWORD_ENCODER.encode(plainPassword);
        return new Password(hashed);
    }

public static Password fromHash(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new InvalidPasswordException(ResourceMessages.PASSWORD_CANNOT_BE_EMPTY.getMessage());
        }
        return new Password(hashedPassword);
    }

public boolean matches(String plainPassword) {
        if (plainPassword == null) {
            return false;
        }
        return PASSWORD_ENCODER.matches(plainPassword, hashedValue);
    }

    @Override
    public String toString() {
        return "[PROTECTED]";
    }
}
