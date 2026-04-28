package com.mateuslll.taskflow.common.exceptions;

public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String message) {
        super(message, "resource_not_found");
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, "resource_not_found", cause);
    }
}
