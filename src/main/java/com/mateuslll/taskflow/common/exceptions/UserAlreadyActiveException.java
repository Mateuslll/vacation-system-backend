package com.mateuslll.taskflow.common.exceptions;


public class UserAlreadyActiveException extends DomainException {
    
    public UserAlreadyActiveException() {
        super("Usuário já está ativo no sistema");
    }
    
    public UserAlreadyActiveException(String userId) {
        super("Usuário " + userId + " já está ativo no sistema");
    }
}
