package com.mateuslll.taskflow.common.exceptions.global;

import com.mateuslll.taskflow.common.exceptions.DomainException;
import com.mateuslll.taskflow.common.exceptions.ForbiddenAccessException;
import com.mateuslll.taskflow.common.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler Test")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Deve retornar Problem Details RFC para acesso proibido")
    void shouldReturnProblemDetailForForbiddenException() {
        HttpServletRequest request = mockRequest("/api/v1/vacation-requests");

        ResponseEntity<ProblemDetail> response = handler.handleForbiddenException(
                new ForbiddenAccessException("Acesso negado"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Forbidden");
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(response.getBody().getDetail()).isEqualTo("Acesso negado");
        assertThat(response.getBody().getType().toString()).isEqualTo("urn:taskflow:problem:forbidden");
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/api/v1/vacation-requests");
        assertThat(response.getBody().getProperties()).containsKey("timestamp");
    }

    @Test
    @DisplayName("Deve retornar Problem Details RFC para recurso não encontrado")
    void shouldReturnProblemDetailForResourceNotFound() {
        HttpServletRequest request = mockRequest("/api/v1/users/123");

        ResponseEntity<ProblemDetail> response = handler.handleResourceNotFoundException(
                new ResourceNotFoundException("Usuário não encontrado"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Not Found");
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getBody().getDetail()).isEqualTo("Usuário não encontrado");
        assertThat(response.getBody().getType().toString()).isEqualTo("urn:taskflow:problem:resource-not-found");
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/api/v1/users/123");
    }

    @Test
    @DisplayName("Deve retornar Problem Details RFC com errors para DomainException")
    void shouldReturnProblemDetailWithErrorsForDomainException() {
        HttpServletRequest request = mockRequest("/api/v1/vacation-requests");
        DomainException exception = new DomainException("Erro de regra de negócio", List.of("Detalhe 1", "Detalhe 2"));

        ResponseEntity<ProblemDetail> response = handler.handleDomainException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Unprocessable Entity");
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(response.getBody().getDetail()).isEqualTo("Erro de regra de negócio");
        assertThat(response.getBody().getType().toString()).isEqualTo("urn:taskflow:problem:domain-rule-violation");
        assertThat(response.getBody().getProperties()).containsKey("errors");
    }

    @Test
    @DisplayName("Deve retornar Problem Details RFC para erro genérico")
    void shouldReturnProblemDetailForGenericException() {
        HttpServletRequest request = mockRequest("/api/v1/auth/login");

        ResponseEntity<ProblemDetail> response = handler.handleGenericException(
                new RuntimeException("Falha inesperada"),
                request
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getBody().getType().toString()).isEqualTo("urn:taskflow:problem:internal-server-error");
        assertThat(response.getBody().getInstance().toString()).isEqualTo("/api/v1/auth/login");
        assertThat(response.getBody().getProperties()).containsKey("timestamp");
    }

    private HttpServletRequest mockRequest(String uri) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(uri);
        return request;
    }
}
