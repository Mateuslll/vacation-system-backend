package com.mateuslll.taskflow.application.controllers.user;

import com.mateuslll.taskflow.application.controllers.docs.UserAPI;
import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;
import com.mateuslll.taskflow.application.usecases.user.activate.ActivateUserRequestDTO;
import com.mateuslll.taskflow.application.usecases.user.activate.ActivateUserUseCase;
import com.mateuslll.taskflow.application.usecases.user.assignmanager.AssignManagerRequestDTO;
import com.mateuslll.taskflow.application.usecases.user.assignmanager.AssignManagerUseCase;
import com.mateuslll.taskflow.application.usecases.user.setroles.SetRolesRequestDTO;
import com.mateuslll.taskflow.application.usecases.user.setroles.SetUserRolesUseCase;
import com.mateuslll.taskflow.application.usecases.user.collaborators.GetCollaboratorsByManagerRequestDTO;
import com.mateuslll.taskflow.application.usecases.user.collaborators.GetCollaboratorsByManagerUseCase;
import com.mateuslll.taskflow.application.usecases.user.create.CreateUserRequestDTO;
import com.mateuslll.taskflow.application.usecases.user.create.CreateUserUseCase;
import com.mateuslll.taskflow.application.usecases.user.deactivate.DeactivateUserRequestDTO;
import com.mateuslll.taskflow.application.usecases.user.deactivate.DeactivateUserUseCase;
import com.mateuslll.taskflow.application.usecases.user.delete.DeleteUserUseCase;
import com.mateuslll.taskflow.application.usecases.user.getall.GetAllUsersRequestDTO;
import com.mateuslll.taskflow.application.usecases.user.getall.GetAllUsersUseCase;
import com.mateuslll.taskflow.application.usecases.user.getall.UserStatusFilter;
import com.mateuslll.taskflow.application.usecases.user.listmanagersadmins.ListManagersAndAdminsUseCase;
import com.mateuslll.taskflow.application.usecases.user.retrieve.byid.GetUserByIdRequestDTO;
import com.mateuslll.taskflow.application.usecases.user.retrieve.byid.GetUserByIdUseCase;
import com.mateuslll.taskflow.application.usecases.user.update.UpdateUserRequestDTO;
import com.mateuslll.taskflow.application.usecases.user.update.UpdateUserUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints para gerenciamento de usuários")
public class UserController implements UserAPI {

    private final GetUserByIdUseCase getUserByIdUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final ActivateUserUseCase activateUserUseCase;
    private final DeactivateUserUseCase deactivateUserUseCase;
    private final AssignManagerUseCase assignManagerUseCase;
    private final SetUserRolesUseCase setUserRolesUseCase;
    private final GetCollaboratorsByManagerUseCase getCollaboratorsByManagerUseCase;
    private final ListManagersAndAdminsUseCase listManagersAndAdminsUseCase;

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody CreateUserRequestDTO request) {
        UserResponseDTO response = createUserUseCase.execute(request);
        return ResponseEntity.status(CREATED).body(response);
    }

    @Override
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(
            @RequestParam(required = false) UserStatusFilter status) {
        
        GetAllUsersRequestDTO request = new GetAllUsersRequestDTO(status);
        List<UserResponseDTO> response = getAllUsersUseCase.execute(request);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("id") UUID userId) {
        GetUserByIdRequestDTO request = new GetUserByIdRequestDTO(userId);
        UserResponseDTO response = getUserByIdUseCase.execute(request);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable("id") UUID userId,
            @Valid @RequestBody UpdateUserRequestDTO request) {

        UserResponseDTO response = updateUserUseCase.execute(request);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UserResponseDTO> activateUser(@PathVariable("id") UUID userId) {
        ActivateUserRequestDTO request = new ActivateUserRequestDTO(userId);
        UserResponseDTO response = activateUserUseCase.execute(request);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<UserResponseDTO> deactivateUser(@PathVariable("id") UUID userId) {
        DeactivateUserRequestDTO request = new DeactivateUserRequestDTO(userId);
        UserResponseDTO response = deactivateUserUseCase.execute(request);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @PutMapping("/{userId}/manager/{managerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> assignManager(
            @PathVariable("userId") UUID userId,
            @PathVariable("managerId") UUID managerId) {

        AssignManagerRequestDTO request = new AssignManagerRequestDTO(userId, managerId);
        UserResponseDTO response = assignManagerUseCase.execute(request);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> assignRole(
            @PathVariable("id") UUID userId,
            @Valid @RequestBody SetRolesRequestDTO request) {

        UserResponseDTO response = setUserRolesUseCase.execute(userId, request);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @GetMapping("/manager/{managerId}/collaborators")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<UserResponseDTO>> getCollaboratorsByManager(
            @PathVariable("managerId") UUID managerId) {

        GetCollaboratorsByManagerRequestDTO request = new GetCollaboratorsByManagerRequestDTO(managerId);
        List<UserResponseDTO> response = getCollaboratorsByManagerUseCase.execute(request);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @GetMapping("/managers-and-admins")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<UserResponseDTO>> getManagersAndAdmins() {
        List<UserResponseDTO> response = listManagersAndAdminsUseCase.execute(null);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID userId) {
        deleteUserUseCase.execute(userId);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}
