package com.mateuslll.taskflow.common.exceptions;

public class InvalidCredentialsException extends UnauthorizedException {

    public InvalidCredentialsException() {
        super("Email ou senha inválidos");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
