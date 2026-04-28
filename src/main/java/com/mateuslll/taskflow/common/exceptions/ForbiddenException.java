package com.mateuslll.taskflow.common.exceptions;

public class ForbiddenException extends BaseException {

    public ForbiddenException(String message) {
        super(message, "forbidden");
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, "forbidden", cause);
    }
}
