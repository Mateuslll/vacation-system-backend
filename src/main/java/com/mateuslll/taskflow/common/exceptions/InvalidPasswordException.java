package com.mateuslll.taskflow.common.exceptions;

public class InvalidPasswordException extends BadRequestException {

    public InvalidPasswordException(String message) {
        super(message);
    }
}
