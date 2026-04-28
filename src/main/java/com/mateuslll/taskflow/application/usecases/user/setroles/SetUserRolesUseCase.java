package com.mateuslll.taskflow.application.usecases.user.setroles;

import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;

import java.util.UUID;

public interface SetUserRolesUseCase {
    UserResponseDTO execute(UUID userId, SetRolesRequestDTO request);
}

