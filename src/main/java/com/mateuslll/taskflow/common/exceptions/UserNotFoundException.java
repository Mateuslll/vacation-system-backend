package com.mateuslll.taskflow.common.exceptions;


public class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException(String userId) {
        super(String.format("Usuário não encontrado para o id: %s", userId));
    }
}
