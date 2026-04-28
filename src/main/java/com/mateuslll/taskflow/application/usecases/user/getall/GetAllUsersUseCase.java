package com.mateuslll.taskflow.application.usecases.user.getall;

import com.mateuslll.taskflow.application.usecases.UseCase;
import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;

import java.util.List;

public interface GetAllUsersUseCase extends UseCase<GetAllUsersRequestDTO, List<UserResponseDTO>> {
}
