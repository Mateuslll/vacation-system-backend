package com.mateuslll.taskflow.application.controllers.docs;

import com.mateuslll.taskflow.application.usecases.bootstrap.dto.CreateAdminRequest;
import com.mateuslll.taskflow.application.usecases.bootstrap.dto.CreateAdminResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Bootstrap", description = "Endpoints para inicialização do sistema")
public interface BootstrapAPI {

    @Operation(
            summary = "Criar administrador via bootstrap",
            description = """
                    Endpoint público para criar administradores do sistema.
                    Não requer autenticação.
                    Útil para setup inicial do sistema e criação de usuários para testes.
                    
                    **Validações:**
                    - **Email**: formato válido e único no sistema
                    - **Senha**: mínimo 8 caracteres, deve conter:
                      - Pelo menos 1 letra maiúscula (A-Z)
                      - Pelo menos 1 letra minúscula (a-z)
                      - Pelo menos 1 número (0-9)
                      - Pelo menos 1 caractere especial (@, $, !, %, *, ?, &, #)
                    - **Nome/Sobrenome**: 2-50 caracteres cada
                    
                    **Segurança:**
                    - Senha criptografada com BCrypt (strength 12)
                    - Usuário criado com status ACTIVE
                    - Role ADMIN atribuída automaticamente
                    - Nenhum token gerado (requer login posterior)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Administrador criado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Recurso já existente (email já cadastrado)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Recurso não encontrado (role ADMIN)"
            )
    })
    ResponseEntity<CreateAdminResponse> createAdmin(@Valid @RequestBody CreateAdminRequest request);
}
