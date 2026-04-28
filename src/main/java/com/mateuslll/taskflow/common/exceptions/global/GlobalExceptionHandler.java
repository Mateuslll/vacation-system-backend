package com.mateuslll.taskflow.common.exceptions.global;

import com.mateuslll.taskflow.common.exceptions.BaseException;
import com.mateuslll.taskflow.common.exceptions.BadRequestException;
import com.mateuslll.taskflow.common.exceptions.DomainException;
import com.mateuslll.taskflow.common.exceptions.ForbiddenException;
import com.mateuslll.taskflow.common.exceptions.ResourceAlreadyExistsException;
import com.mateuslll.taskflow.common.exceptions.ResourceNotFoundException;
import com.mateuslll.taskflow.common.exceptions.UnauthorizedException;
import com.mateuslll.taskflow.common.exceptions.UserAlreadyActiveException;
import com.mateuslll.taskflow.common.exceptions.UserAlreadyInactiveException;
import com.mateuslll.taskflow.common.exceptions.UserBlockedException;
import com.mateuslll.taskflow.common.exceptions.VacationAlreadyProcessedException;
import com.mateuslll.taskflow.common.exceptions.VacationAlreadyStartedException;
import com.mateuslll.taskflow.common.exceptions.VacationOverlapException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String PROBLEM_TYPE_BASE = "urn:taskflow:problem:";

@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationExceptions(
        MethodArgumentNotValidException ex,
        HttpServletRequest request) {
    List<Map<String, String>> errors = ex.getBindingResult()
        .getAllErrors()
        .stream()
        .map(error -> {
            String fieldName = error instanceof FieldError fieldError ? fieldError.getField() : "request";
            String errorMessage = error.getDefaultMessage() != null
                ? error.getDefaultMessage()
                : "Valor inválido";
            return Map.of(
                "field", fieldName,
                "message", errorMessage
            );
        })
        .toList();

    ProblemDetail problemDetail = buildProblem(
        HttpStatus.BAD_REQUEST,
        "Validation Error",
        "Erro de validação nos campos fornecidos",
        "validation-error",
        request
    );
    problemDetail.setProperty("errors", errors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

@ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ProblemDetail> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage(),
                "bad-request",
                request
        );
    }

@ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage(),
                "resource-not-found",
                request
        );
    }

@ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException ex,
            HttpServletRequest request) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "Conflict",
                ex.getMessage(),
                "resource-already-exists",
                request
        );
    }

@ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ProblemDetail> handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                ex.getMessage(),
                "unauthorized",
                request
        );
    }

@ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ProblemDetail> handleForbiddenException(ForbiddenException ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.FORBIDDEN,
                "Forbidden",
                ex.getMessage(),
                "forbidden",
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {
        log.debug("Acesso negado: {}", ex.getMessage());
        return buildResponse(
                HttpStatus.FORBIDDEN,
                "Forbidden",
                "Não tem permissão para aceder a este recurso.",
                "forbidden",
                request
        );
    }

@ExceptionHandler(UserAlreadyActiveException.class)
        public ResponseEntity<ProblemDetail> handleUserAlreadyActiveException(
            UserAlreadyActiveException ex,
            HttpServletRequest request) {
        ProblemDetail problemDetail = buildProblem(
            HttpStatus.CONFLICT,
            "Conflict",
            ex.getMessage(),
            "user-already-active",
            request
        );
        problemDetail.setProperty("errors", List.of(Map.of(
            "status", "ACTIVE",
            "action", "activate",
            "hint", "Usuário já está ativo. Use o endpoint de desativação se necessário."
        )));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

@ExceptionHandler(UserAlreadyInactiveException.class)
        public ResponseEntity<ProblemDetail> handleUserAlreadyInactiveException(
            UserAlreadyInactiveException ex,
            HttpServletRequest request) {
        ProblemDetail problemDetail = buildProblem(
            HttpStatus.CONFLICT,
            "Conflict",
            ex.getMessage(),
            "user-already-inactive",
            request
        );
        problemDetail.setProperty("errors", List.of(Map.of(
            "status", "INACTIVE",
            "action", "deactivate",
            "hint", "Usuário já está inativo. Use o endpoint de ativação para reativar."
        )));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

@ExceptionHandler(UserBlockedException.class)
        public ResponseEntity<ProblemDetail> handleUserBlockedException(UserBlockedException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = buildProblem(
            HttpStatus.FORBIDDEN,
            "Forbidden",
            ex.getMessage(),
            "user-blocked",
            request
        );
        problemDetail.setProperty("errors", List.of(Map.of(
            "status", "BLOCKED",
            "reason", "Usuário bloqueado por violação de política ou decisão administrativa",
            "hint", "Usuários bloqueados não podem ser ativados/desativados. Contate o administrador do sistema."
        )));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problemDetail);
    }

@ExceptionHandler(VacationOverlapException.class)
        public ResponseEntity<ProblemDetail> handleVacationOverlapException(VacationOverlapException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = buildProblem(
            HttpStatus.CONFLICT,
            "Conflict",
            ex.getMessage(),
            "vacation-overlap",
            request
        );
        problemDetail.setProperty("errors", List.of(Map.of(
            "conflictingUser", ex.getConflictingUserName(),
            "conflictingPeriod", Map.of(
                "startDate", ex.getConflictingStartDate(),
                "endDate", ex.getConflictingEndDate()
            ),
            "conflictingVacationId", ex.getConflictingVacationId(),
            "hint", "Já existe férias aprovadas que se sobrepõem ao período solicitado. Escolha datas diferentes ou aguarde a conclusão das férias existentes."
        )));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

@ExceptionHandler(VacationAlreadyProcessedException.class)
    public ResponseEntity<ProblemDetail> handleVacationAlreadyProcessedException(
            VacationAlreadyProcessedException ex,
            HttpServletRequest request) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "Conflict",
                ex.getMessage(),
                "vacation-already-processed",
                request
        );
    }

@ExceptionHandler(VacationAlreadyStartedException.class)
    public ResponseEntity<ProblemDetail> handleVacationAlreadyStartedException(
            VacationAlreadyStartedException ex,
            HttpServletRequest request) {
        return buildResponse(
                HttpStatus.CONFLICT,
                "Conflict",
                ex.getMessage(),
                "vacation-already-started",
                request
        );
    }

@ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomainException(DomainException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = buildProblem(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Unprocessable Entity",
                ex.getMessage(),
                "domain-rule-violation",
                request
        );
        if (ex.getDetails() != null && !ex.getDetails().isEmpty()) {
            problemDetail.setProperty("errors", ex.getDetails());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problemDetail);
    }

@ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage(),
                "invalid-argument",
                request
        );
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ProblemDetail> handleBaseException(BaseException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String title = "Bad Request";
        String type = "business-error";

        if (ex instanceof ResourceNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            title = "Not Found";
            type = "resource-not-found";
        } else if (ex instanceof ResourceAlreadyExistsException) {
            status = HttpStatus.CONFLICT;
            title = "Conflict";
            type = "resource-already-exists";
        } else if (ex instanceof UnauthorizedException) {
            status = HttpStatus.UNAUTHORIZED;
            title = "Unauthorized";
            type = "unauthorized";
        } else if (ex instanceof ForbiddenException) {
            status = HttpStatus.FORBIDDEN;
            title = "Forbidden";
            type = "forbidden";
        } else if (ex instanceof DomainException) {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            title = "Unprocessable Entity";
            type = "domain-rule-violation";
        }

        return buildResponse(
                status,
                title,
                ex.getMessage(),
                type,
                request
        );
    }

@ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Erro inesperado ao processar requisição", ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Ocorreu um erro inesperado. Por favor, contate o suporte.",
                "internal-server-error",
                request
        );
    }

    private ResponseEntity<ProblemDetail> buildResponse(
            HttpStatus status,
            String title,
            String detail,
            String type,
            HttpServletRequest request
    ) {
        ProblemDetail problemDetail = buildProblem(status, title, detail, type, request);
        return ResponseEntity.status(status).body(problemDetail);
    }

    private ProblemDetail buildProblem(
            HttpStatus status,
            String title,
            String detail,
            String type,
            HttpServletRequest request
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create(PROBLEM_TYPE_BASE + type));
        if (request != null) {
            problemDetail.setInstance(URI.create(request.getRequestURI()));
        }
        problemDetail.setProperty("timestamp", Instant.now().toString());
        return problemDetail;
    }
}
