package com.mateuslll.taskflow.application.usecases.user.collaborators;

import com.mateuslll.taskflow.application.usecases.UseCase;
import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;

import java.util.List;

public interface GetCollaboratorsByManagerUseCase extends UseCase<GetCollaboratorsByManagerRequestDTO, List<UserResponseDTO>> {
}
