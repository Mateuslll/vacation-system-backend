package com.mateuslll.taskflow.application.usecases.user.retrieve.byid;

import com.mateuslll.taskflow.application.usecases.UseCase;
import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;

public interface GetUserByIdUseCase extends UseCase<GetUserByIdRequestDTO, UserResponseDTO> {
}
