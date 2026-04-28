package com.mateuslll.taskflow.common.exceptions;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final String errorCode;

    public BaseException(String message) {
        super(message);
        this.errorCode = "business_error";
    }

    public BaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "business_error";
    }

    public BaseException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
