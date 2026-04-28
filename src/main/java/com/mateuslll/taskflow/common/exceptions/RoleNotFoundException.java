package com.mateuslll.taskflow.common.exceptions;


public class RoleNotFoundException extends ResourceNotFoundException {

    public RoleNotFoundException(String roleId) {
        super(String.format("Role não encontrado para o id: %s", roleId));
    }
}
