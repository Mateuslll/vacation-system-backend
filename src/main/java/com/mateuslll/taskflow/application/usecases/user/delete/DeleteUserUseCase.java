package com.mateuslll.taskflow.application.usecases.user.delete;

import java.util.UUID;

public interface DeleteUserUseCase {
    void execute(UUID userId);
}
