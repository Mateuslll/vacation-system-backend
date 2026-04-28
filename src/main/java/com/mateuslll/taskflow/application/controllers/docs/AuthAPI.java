package com.mateuslll.taskflow.application.controllers.docs;

import com.mateuslll.taskflow.application.usecases.auth.dto.AuthResponse;
import com.mateuslll.taskflow.application.usecases.auth.dto.LoginRequest;
import com.mateuslll.taskflow.application.usecases.auth.dto.LoginResponse;
import com.mateuslll.taskflow.application.usecases.auth.dto.LogoutRequest;
import com.mateuslll.taskflow.application.usecases.auth.dto.RefreshTokenRequest;
import com.mateuslll.taskflow.application.usecases.auth.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

public interface AuthAPI {

    @Operation(
            summary = "Login de usuário",
            description = """
                    Autentica um usuário no sistema usando email e senha, retornando tokens JWT.
                    
                    **Fluxo:**
                    1. Valida formato do email e senha
                    2. Busca usuário no banco de dados pelo email
                    3. Verifica a senha usando BCrypt
                    4. Valida se o usuário está ativo (status ACTIVE)
                    5. Gera access token JWT (validade: 1 hora)
                    6. Gera refresh token JWT (validade: 7 dias)
                    7. Retorna apenas os tokens de autenticação
                    
                    **Tokens retornados:**
                    - **Access Token**: Usar no header Authorization para acessar endpoints protegidos
                      - Formato: `Authorization: Bearer <access_token>`
                      - Validade: 1 hora (3600 segundos)
                      - Contém: userId, email, name, roles, permissions
                    
                    - **Refresh Token**: Usar para renovar access token expirado
                      - Endpoint: POST /api/v1/auth/refresh
                      - Validade: 7 dias (604800 segundos)
                      - Permite obter novo access token sem novo login
                    
                    **Validações:**
                    - Email: formato válido obrigatório
                    - Senha: mínimo 8 caracteres
                    - Usuário: deve existir e estar ativo (status ACTIVE)
                    
                    **Regras de Negócio:**
                    - Credenciais inválidas retornam 401 sem especificar se email ou senha está incorreto
                    - Usuários inativos (status INACTIVE ou BLOCKED) não podem fazer login
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciais inválidas"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos"
            )
    })
        ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request);

    @Operation(
            summary = "Registrar novo usuário",
            description = """
                    Cria uma nova conta de usuário no sistema com role USER padrão.
                    
                    **Fluxo:**
                    1. Valida dados de entrada (nome, email, senha)
                    2. Verifica se email já está cadastrado
                    3. Criptografa senha usando BCrypt (strength 12)
                    4. Cria usuário com status ACTIVE
                    5. Atribui role USER automaticamente
                    6. Salva no banco de dados
                    7. Gera tokens JWT (login automático)
                    8. Retorna tokens e informações do usuário
                    
                    **Dados cadastrados:**
                    - Email (único no sistema)
                    - Senha (criptografada com BCrypt)
                    - Nome e sobrenome
                    - Status inicial: ACTIVE
                    - Role inicial: USER
                    - Data de criação (timestamp)
                    
                    **Validações:**
                    - **Email**: formato válido e único no sistema
                    - **Senha**: mínimo 8 caracteres, deve conter:
                      - Pelo menos 1 letra maiúscula (A-Z)
                      - Pelo menos 1 letra minúscula (a-z)
                      - Pelo menos 1 número (0-9)
                      - Pelo menos 1 caractere especial (@, $, !, %, *, ?, &, #)
                    - **Nome/Sobrenome**: 2-50 caracteres cada
                    
                    **Regras de Negócio:**
                    - Email deve ser único (retorna 409 se já existir)
                    - Senha é criptografada antes de salvar (nunca armazenada em texto plano)
                    - Usuário é criado com status ACTIVE (pode fazer login imediatamente)
                    - Role USER é atribuída automaticamente
                    - Após registro, usuário é automaticamente autenticado (retorna tokens)
                    
                    **Permissões da role USER:**
                    - Ver e editar próprio perfil
                    - Criar solicitações de férias
                    - Cancelar próprias solicitações
                    - Ver histórico de férias
                    - Alterar própria senha
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário criado com sucesso",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Recurso já existente (email já cadastrado)"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Regra de negócio violada"
            )
    })
    ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request);

    @Operation(
            summary = "Renovar access token",
            description = """
                    Gera um novo access token usando um refresh token válido (rotação de tokens).
                    
                    **Fluxo:**
                    1. Valida o refresh token fornecido
                    2. Verifica assinatura e expiração do token
                    3. Extrai informações do usuário (userId, email)
                    4. Busca usuário no banco de dados
                    5. Verifica se usuário ainda está ativo (inativo/bloqueado é tratado como token inválido)
                    6. Gera novo access token (validade: 1 hora)
                    7. Gera novo refresh token (rotação - validade: 7 dias)
                    8. Retorna novos tokens
                    
                    **Rotação de Refresh Token:**
                    - Cada vez que o refresh é usado, um NOVO refresh token é gerado
                    - Refresh token antigo deve ser descartado (one-time use)
                    - Isso aumenta a segurança, limitando janela de ataque
                    
                    **Validações:**
                    - Refresh token: obrigatório e válido
                    - Token não pode estar expirado
                    - Usuário do token deve existir no banco
                    - Usuário deve estar ativo (status ACTIVE)
                    
                    **Regras de Negócio:**
                    - Refresh token expirado retorna 401 (usuário deve fazer login novamente)
                    - Se usuário foi desativado/bloqueado, refresh falha como credencial inválida
                    - Novo refresh token é gerado (rotação) para aumentar segurança
                    - Access token antigo é invalidado automaticamente
                    
                    **Segurança:**
                    - Refresh token tem validade longa (7 dias) mas é rotacionado
                    - Implementado blacklist de tokens em memória (server-side)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tokens renovados com sucesso",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado ou token inválido/expirado"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Recurso não encontrado (usuário do token)"
            ),
            
    })
    ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request);

    @Operation(
            summary = "Obter usuário autenticado",
            description = """
                    Retorna informações do usuário atual extraídas do token JWT no header.
                    
                    **Fluxo:**
                    1. Extrai token JWT do header Authorization
                    2. Valida token (assinatura, expiração)
                    3. Extrai claims do token (userId, email, roles, etc)
                    4. Retorna informações do usuário autenticado
                    
                    **Quando usar:**
                    - Obter dados do usuário logado
                    - Validar se token ainda é válido
                    - Carregar informações do perfil no frontend
                    - Verificar roles/permissões do usuário atual
                    
                    **Autenticação:**
                    - Requer token JWT válido no header
                    - Formato: `Authorization: Bearer <access_token>`
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dados do usuário autenticado retornados com sucesso"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autenticado ou token inválido/expirado"
            )
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<Map<String, Object>> getCurrentUser();

    @Operation(
            summary = "Logout de usuário",
            description = """
                    Invalida o refresh token fazendo logout server-side com blacklist em memória.
                    
                    **Fluxo:**
                    1. Recebe refresh token no body da requisição
                    2. Valida formato do refresh token
                    3. Adiciona token à blacklist em memória (invalidação server-side)
                    4. Token fica inválido até sua expiração natural
                    5. Limpa SecurityContext do Spring Security
                    6. Retorna 204 No Content (sucesso sem corpo)
                    
                    **Segurança:**
                    - Tentativa de usar token após logout resulta em 401 Unauthorized
                    - Verificação de blacklist em toda requisição autenticada
                    
                    **Autenticação:**
                    - Não requer token no header (endpoint público)
                    - Recebe refresh token no body
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Logout realizado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos"
            )
    })
    ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody LogoutRequest request);
}
