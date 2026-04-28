package com.mateuslll.taskflow.common.exceptions;

public class ResourceAlreadyExistsException extends BaseException {

    public ResourceAlreadyExistsException(String message) {
        super(message, "resource_conflict");
    }

    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, "resource_conflict", cause);
    }
}
