package com.mateuslll.taskflow.common.exceptions;


public class UserAlreadyInactiveException extends DomainException {
    
    public UserAlreadyInactiveException() {
        super("Usuário já está inativo no sistema");
    }
    
    public UserAlreadyInactiveException(String userId) {
        super("Usuário " + userId + " já está inativo no sistema");
    }
}
