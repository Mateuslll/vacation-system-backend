package com.mateuslll.taskflow.application.usecases.user.assignrole;

import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;

import java.util.UUID;

public interface AssignRoleUseCase {
    UserResponseDTO execute(UUID userId, AssignRoleRequestDTO request);
}
