package com.mateuslll.taskflow.common.exceptions;

public class TokenExpiredException extends UnauthorizedException {

    public TokenExpiredException() {
        super("Token JWT expirou. Faça login novamente ou use o refresh token.");
    }

    public TokenExpiredException(String message) {
        super(message);
    }
}
