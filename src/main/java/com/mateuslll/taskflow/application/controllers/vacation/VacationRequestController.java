package com.mateuslll.taskflow.application.controllers.vacation;

import com.mateuslll.taskflow.application.controllers.docs.VacationRequestAPI;
import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;
import com.mateuslll.taskflow.application.usecases.vacation.approve.ApproveVacationRequestUseCase;
import com.mateuslll.taskflow.application.usecases.vacation.cancel.CancelVacationRequestUseCase;
import com.mateuslll.taskflow.application.usecases.vacation.create.CreateVacationRequestUseCase;
import com.mateuslll.taskflow.application.usecases.vacation.create.dto.CreateVacationRequestRequestDTO;
import com.mateuslll.taskflow.application.usecases.vacation.getapproved.ApprovedVacationPeriodDTO;
import com.mateuslll.taskflow.application.usecases.vacation.getapproved.GetApprovedVacationsRequestDTO;
import com.mateuslll.taskflow.application.usecases.vacation.getapproved.GetApprovedVacationsUseCase;
import com.mateuslll.taskflow.application.usecases.vacation.listall.ListAllVacationRequestsUseCase;
import com.mateuslll.taskflow.application.usecases.vacation.listmy.ListMyVacationRequestsUseCase;
import com.mateuslll.taskflow.application.usecases.vacation.listteam.ListTeamVacationRequestsUseCase;
import com.mateuslll.taskflow.application.usecases.vacation.reject.RejectVacationRequestRequestDTO;
import com.mateuslll.taskflow.application.usecases.vacation.reject.RejectVacationRequestUseCase;
import com.mateuslll.taskflow.application.usecases.vacation.getVacationRequestById.GetVacationRequestByIdRequestDTO;
import com.mateuslll.taskflow.application.usecases.vacation.getVacationRequestById.GetVacationRequestByIdUseCase;
import com.mateuslll.taskflow.application.usecases.vacation.update.UpdateVacationRequestRequestDTO;
import com.mateuslll.taskflow.application.usecases.vacation.update.UpdateVacationRequestUseCase;
import com.mateuslll.taskflow.common.exceptions.UnauthorizedException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/vacation-requests")
@RequiredArgsConstructor
@Tag(name = "Vacation Requests", description = "Endpoints para gerenciamento de solicitações de férias")
public class VacationRequestController implements VacationRequestAPI {

    private final CreateVacationRequestUseCase createVacationRequestUseCase;
    private final ApproveVacationRequestUseCase approveVacationRequestUseCase;
    private final RejectVacationRequestUseCase rejectVacationRequestUseCase;
    private final CancelVacationRequestUseCase cancelVacationRequestUseCase;
    private final GetVacationRequestByIdUseCase getVacationRequestByIdUseCase;
    private final GetApprovedVacationsUseCase getApprovedVacationsUseCase;
    private final ListAllVacationRequestsUseCase listAllVacationRequestsUseCase;
    private final ListMyVacationRequestsUseCase listMyVacationRequestsUseCase;
    private final ListTeamVacationRequestsUseCase listTeamVacationRequestsUseCase;
    private final UpdateVacationRequestUseCase updateVacationRequestUseCase;

    @Override
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VacationRequestResponseDTO> createVacationRequest(
            @Valid @RequestBody CreateVacationRequestRequestDTO request) {
        UUID requesterId = extractUserIdFromSecurityContext();
        VacationRequestResponseDTO response = createVacationRequestUseCase.execute(requesterId, request);
        return ResponseEntity.status(CREATED).body(response);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VacationRequestResponseDTO> getVacationRequestById(
            @PathVariable("id") UUID vacationRequestId) {
        UUID requesterId = extractUserIdFromSecurityContext();
        
        GetVacationRequestByIdRequestDTO request = new GetVacationRequestByIdRequestDTO(vacationRequestId, requesterId);
        VacationRequestResponseDTO response = getVacationRequestByIdUseCase.execute(request);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<VacationRequestResponseDTO> approveVacationRequest(
            @PathVariable("id") UUID vacationRequestId) {
        UUID managerId = extractUserIdFromSecurityContext();
        VacationRequestResponseDTO response = approveVacationRequestUseCase.execute(vacationRequestId, managerId);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<VacationRequestResponseDTO> rejectVacationRequest(
            @PathVariable("id") UUID vacationRequestId,
            @Valid @RequestBody RejectVacationRequestRequestDTO request) {

        UUID managerId = extractUserIdFromSecurityContext();
        VacationRequestResponseDTO response = rejectVacationRequestUseCase.execute(
                vacationRequestId, managerId, request.rejectionReason());
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VacationRequestResponseDTO> updateVacationRequest(
            @PathVariable("id") UUID vacationRequestId,
            @Valid @RequestBody UpdateVacationRequestRequestDTO request) {
        UUID requesterId = extractUserIdFromSecurityContext();
        VacationRequestResponseDTO response = updateVacationRequestUseCase.execute(
                vacationRequestId, requesterId, request);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @PutMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VacationRequestResponseDTO> cancelVacationRequest(
            @PathVariable("id") UUID vacationRequestId) {

        UUID requesterId = extractUserIdFromSecurityContext();
        VacationRequestResponseDTO response = cancelVacationRequestUseCase.execute(vacationRequestId, requesterId);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<VacationRequestResponseDTO>> getAllVacationRequests(
            @RequestParam(required = false) com.mateuslll.taskflow.domain.enums.RequestStatus status,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<VacationRequestResponseDTO> response = listAllVacationRequestsUseCase.execute(status, userId, startDate, endDate);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<VacationRequestResponseDTO>> getMyVacationRequests() {
        UUID userId = extractUserIdFromSecurityContext();
        
        List<VacationRequestResponseDTO> response = listMyVacationRequestsUseCase.execute(userId);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<VacationRequestResponseDTO>> getTeamVacationRequests() {
        UUID requesterId = extractUserIdFromSecurityContext();
        
        List<VacationRequestResponseDTO> response = listTeamVacationRequestsUseCase.execute(requesterId);
        return ResponseEntity.status(OK).body(response);
    }

    @Override
    @GetMapping("/approved")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ApprovedVacationPeriodDTO>> getApprovedVacations(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        GetApprovedVacationsRequestDTO request = new GetApprovedVacationsRequestDTO(startDate, endDate);
        List<ApprovedVacationPeriodDTO> response = getApprovedVacationsUseCase.execute(request);
        return ResponseEntity.status(OK).body(response);
    }

    private UUID extractUserIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Usuário não autenticado");
        }
        
        String userIdStr = authentication.getName();
        return UUID.fromString(userIdStr);
    }
}
