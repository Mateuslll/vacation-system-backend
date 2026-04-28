package com.mateuslll.taskflow.common.exceptions;

public class EmailAlreadyRegisteredException extends ResourceAlreadyExistsException {
    
    public EmailAlreadyRegisteredException(String email) {
        super("Email já cadastrado: " + email);
    }
}
