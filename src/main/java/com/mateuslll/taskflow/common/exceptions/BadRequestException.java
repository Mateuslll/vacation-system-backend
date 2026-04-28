package com.mateuslll.taskflow.common.exceptions;

public class BadRequestException extends BaseException {

    public BadRequestException(String message) {
        super(message, "bad_request");
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, "bad_request", cause);
    }
}
