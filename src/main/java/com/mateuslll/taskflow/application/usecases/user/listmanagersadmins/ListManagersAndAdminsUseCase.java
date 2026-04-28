package com.mateuslll.taskflow.application.usecases.user.listmanagersadmins;

import com.mateuslll.taskflow.application.usecases.UseCase;
import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;

import java.util.List;

public interface ListManagersAndAdminsUseCase extends UseCase<Void, List<UserResponseDTO>> {
}
