package com.mateuslll.taskflow.application.usecases.bootstrap.mappers;

import com.mateuslll.taskflow.application.usecases.bootstrap.CreateAdminInput;
import com.mateuslll.taskflow.application.usecases.bootstrap.CreateAdminOutput;
import com.mateuslll.taskflow.application.usecases.bootstrap.dto.CreateAdminRequest;
import com.mateuslll.taskflow.application.usecases.bootstrap.dto.CreateAdminResponse;
import org.springframework.stereotype.Component;

@Component
public class BootstrapMapper {

    public CreateAdminInput toInput(CreateAdminRequest request) {
        return new CreateAdminInput(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password()
        );
    }

    public CreateAdminResponse toResponse(CreateAdminOutput output) {
        return new CreateAdminResponse(
                output.id(),
                output.firstName(),
                output.lastName(),
                output.email(),
                output.message()
        );
    }
}
