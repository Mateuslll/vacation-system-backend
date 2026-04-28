package com.mateuslll.taskflow.application.controllers.bootstrap;

import com.mateuslll.taskflow.application.controllers.docs.BootstrapAPI;
import com.mateuslll.taskflow.application.usecases.bootstrap.CreateAdmin;
import com.mateuslll.taskflow.application.usecases.bootstrap.CreateAdminInput;
import com.mateuslll.taskflow.application.usecases.bootstrap.CreateAdminOutput;
import com.mateuslll.taskflow.application.usecases.bootstrap.dto.CreateAdminRequest;
import com.mateuslll.taskflow.application.usecases.bootstrap.dto.CreateAdminResponse;
import com.mateuslll.taskflow.application.usecases.bootstrap.mappers.BootstrapMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bootstrap")
@RequiredArgsConstructor
public class BootstrapController implements BootstrapAPI {

    private final CreateAdmin createAdminUseCase;
    private final BootstrapMapper mapper;

    @Override
    @PostMapping("/create-admin")
    public ResponseEntity<CreateAdminResponse> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        CreateAdminInput input = mapper.toInput(request);
        CreateAdminOutput output = createAdminUseCase.execute(input);
        CreateAdminResponse response = mapper.toResponse(output);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
