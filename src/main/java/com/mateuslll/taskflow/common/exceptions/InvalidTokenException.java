package com.mateuslll.taskflow.common.exceptions;

public class InvalidTokenException extends UnauthorizedException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException() {
        super("Token inválido ou expirado");
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidTokenException malformed() {
        return new InvalidTokenException("Token JWT está malformado");
    }

    public static InvalidTokenException invalidSignature() {
        return new InvalidTokenException("Assinatura do token é inválida");
    }

    public static InvalidTokenException unsupportedToken() {
        return new InvalidTokenException("Tipo de token não suportado");
    }
}
