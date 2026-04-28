package com.mateuslll.taskflow.common.exceptions;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String message) {
        super(message, "unauthorized");
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, "unauthorized", cause);
    }
}
