package com.mateuslll.taskflow.common.exceptions;

public class UserBlockedException extends ForbiddenException {
    
    public UserBlockedException() {
        super("Usuário está bloqueado. Contate o administrador do sistema");
    }
    
    public UserBlockedException(String userId) {
        super("Usuário " + userId + " está bloqueado. Contate o administrador do sistema");
    }
}
