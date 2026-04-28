package com.mateuslll.taskflow.application.controllers.docs;

import com.mateuslll.taskflow.application.usecases.user.UserResponseDTO;
import com.mateuslll.taskflow.application.usecases.user.create.CreateUserRequestDTO;
import com.mateuslll.taskflow.application.usecases.user.setroles.SetRolesRequestDTO;
import com.mateuslll.taskflow.application.usecases.user.update.UpdateUserRequestDTO;
import com.mateuslll.taskflow.application.usecases.user.getall.UserStatusFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface UserAPI {

    @Operation(
            summary = "Criar colaborador (ADMIN)",
            description = """
                    Cria um novo colaborador no sistema.
                    Este endpoint é exclusivo para ADMIN.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Apenas ADMIN pode criar usuários"),
            @ApiResponse(responseCode = "409", description = "Recurso já existente (email já cadastrado)")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody CreateUserRequestDTO request);

    @Operation(
            summary = "Listar todos os usuários com filtro de status",
            description = """
                    Lista todos os usuários do sistema com filtro opcional de status.
                    
                    **Filtros disponíveis via dropdown:**
                    - **ACTIVE**: Lista apenas usuários ativos (explícito)
                    - **INACTIVE**: Lista apenas usuários inativos
                    - **ALL**: Lista todos os usuários (ativos + inativos)
                    
                    **Permissões:**
                    - Usuários comuns: podem ver apenas usuários ACTIVE
                    - ADMIN: pode filtrar por ACTIVE, INACTIVE ou ALL
                    
                    **Casos de Uso:**
                    - Listar colaboradores ativos para seleção
                    - ADMIN visualizar usuários desativados
                    - ADMIN gerar relatórios com todos os usuários
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para visualizar usuários INACTIVE ou ALL")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<List<UserResponseDTO>> getAllUsers(
            @RequestParam(required = false) UserStatusFilter status
    );

    @Operation(
            summary = "Buscar usuário por ID",
            description = """
                    Retorna os dados completos de um usuário pelo seu identificador único.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (usuário)")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<UserResponseDTO> getUserById(@PathVariable("id") UUID userId);

    @Operation(
            summary = "Atualizar dados do usuário",
            description = """
                    Atualiza as informações de perfil de um usuário existente.
                    
                    **Fluxo:**
                    1. Valida o ID do usuário no path e no body
                    2. Busca o usuário no banco de dados
                    3. Valida os novos dados
                    4. Atualiza firstName, lastName, position
                    5. Salva as alterações
                    6. Retorna os dados atualizados
                    
                    **Regras de Negócio:**
                    - ID da URL deve corresponder ao ID do body
                    - Usuário deve existir no sistema
                    - Nome e sobrenome são obrigatórios
                    - Email não pode ser alterado por este endpoint
                    - Roles não podem ser alteradas por este endpoint
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (usuário)"),
            @ApiResponse(responseCode = "422", description = "Regra de negócio violada")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable("id") UUID userId,
            @Valid @RequestBody UpdateUserRequestDTO request
    );

    @Operation(
            summary = "Ativar usuário",
            description = """
                    Ativa um usuário previamente desativado, restaurando seu acesso ao sistema.
                    
                    **Fluxo:**
                    1. Busca o usuário pelo ID
                    2. Verifica se o usuário está INACTIVE
                    3. Altera o status para ACTIVE
                    4. Salva as alterações
                    5. Retorna os dados atualizados
                    
                    **Regras de Negócio:**
                    - Usuário deve existir no sistema
                    - Apenas usuários INACTIVE podem ser ativados
                    - Após ativação, usuário pode fazer login
                    - Histórico de login é preservado
                    
                    **Permissões necessárias:**
                    - Requer role ADMIN ou MANAGER
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário ativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (usuário)"),
            @ApiResponse(responseCode = "409", description = "Conflito de estado (usuário já está ativo)"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para ativar usuário")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<UserResponseDTO> activateUser(@PathVariable("id") UUID userId);

    @Operation(
            summary = "Desativar usuário",
            description = """
                    Desativa um usuário, bloqueando seu acesso ao sistema sem deletar seus dados.
                    
                    **Fluxo:**
                    1. Busca o usuário pelo ID
                    2. Verifica se o usuário está ACTIVE
                    3. Altera o status para INACTIVE
                    4. Invalida tokens de autenticação ativos
                    5. Salva as alterações
                    6. Retorna os dados atualizados
                    
                    **Regras de Negócio:**
                    - Usuário deve existir no sistema
                    - Apenas usuários ACTIVE podem ser desativados
                    - Desativação não exclui dados do usuário
                    - Solicitações de férias pendentes são mantidas
                    - Usuário desativado não pode fazer login
                    - Tokens JWT ativos são invalidados
                    
                    **Permissões necessárias:**
                    - Requer role ADMIN ou MANAGER
                    - Managers não podem desativar outros managers ou admins
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (usuário)"),
            @ApiResponse(responseCode = "409", description = "Conflito de estado (usuário já está inativo)"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para desativar usuário")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<UserResponseDTO> deactivateUser(@PathVariable("id") UUID userId);

    @Operation(
            summary = "Associar manager a colaborador",
            description = """
                    Associa um manager a um colaborador, criando a hierarquia organizacional.
                    APENAS ADMIN pode executar esta operação.
                    
                    **Fluxo:**
                    1. Valida que o colaborador existe
                    2. Valida que o manager existe
                    3. Verifica que o manager tem role MANAGER ou ADMIN
                    4. Associa o manager ao colaborador
                    5. Salva a alteração no banco
                    6. Retorna o colaborador atualizado
                    
                    **Validações:**
                    - userId e managerId devem existir no sistema
                    - managerId deve ter role MANAGER ou ADMIN
                    - Usuário não pode ser manager de si mesmo
                    
                    **Regras de Negócio:**
                    - Apenas ADMIN pode associar managers
                    - Manager deve ter role MANAGER ou ADMIN
                    - Um colaborador pode ter apenas um manager
                    - Manager pode gerenciar múltiplos colaboradores
                    - Necessário para controle de aprovação de férias
                    
                    **Exemplo de Uso:**
                    ```
                    PUT /users/{userId}/manager/{managerId}
                    - userId: UUID do colaborador
                    - managerId: UUID do manager
                    ```
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Manager associado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (colaborador ou manager)"),
            @ApiResponse(responseCode = "403", description = "Apenas ADMIN pode associar managers")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<UserResponseDTO> assignManager(
            @PathVariable("userId") UUID userId,
            @PathVariable("managerId") UUID managerId
    );

    @Operation(
            summary = "Atribuir role a usuário",
            description = """
                    Atribui uma role (papel/perfil) a um usuário existente.
                    APENAS ADMIN pode executar esta operação.
                    
                    **Fluxo:**
                    1. Recebe o userId da URL
                    2. Busca o usuário pelo ID
                    3. Valida a lista de roles (USER, MANAGER, ADMIN)
                    4. Busca as roles no sistema
                    5. Substitui o conjunto de roles do usuário (adiciona e remove conforme necessário)
                    7. Salva as alterações
                    8. Retorna os dados atualizados
                    
                    **Roles Disponíveis:**
                    - **USER**: Colaborador comum (pode criar e gerenciar próprios pedidos de férias)
                    - **MANAGER**: Gerente (pode aprovar/rejeitar férias de seus colaboradores)
                    - **ADMIN**: Administrador (acesso total ao sistema, gerencia usuários e todas as férias)
                    
                    **Validações:**
                    - userId (da URL) deve existir no sistema
                    - roles deve conter apenas: USER, MANAGER ou ADMIN
                    - roles não pode ser vazia
                    
                    **Regras de Negócio:**
                    - Apenas ADMIN pode atribuir roles
                    - Um usuário pode ter múltiplas roles
                    - Esta operação define o conjunto de roles (pode remover roles existentes)
                    
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role atribuída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (usuário ou role)"),
            @ApiResponse(responseCode = "403", description = "Apenas ADMIN pode atribuir roles")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<UserResponseDTO> assignRole(
            @PathVariable("id") UUID userId,
            @Valid @RequestBody SetRolesRequestDTO request
    );

    @Operation(
            summary = "Listar colaboradores de um manager",
            description = """
                    Lista todos os colaboradores associados a um manager específico.
                    
                    **Fluxo:**
                    1. Valida que o manager existe
                    2. Busca todos os usuários que têm manager_id igual ao managerId fornecido
                    3. Retorna a lista de colaboradores
                    
                    **Permissões:**
                    - **MANAGER**: Pode listar apenas seus próprios colaboradores
                    - **ADMIN**: Pode listar colaboradores de qualquer manager
                    
                    **Regras de Negócio:**
                    - Retorna lista vazia se o manager não tem colaboradores
                    - Manager deve existir no sistema
                    - Colaboradores incluem todos os usuários com manager_id = managerId
                   
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de colaboradores retornada com sucesso (pode estar vazia)"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (manager)"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para listar colaboradores deste manager")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<List<UserResponseDTO>> getCollaboratorsByManager(
            @PathVariable("managerId") UUID managerId
    );

    @Operation(
            summary = "Listar Managers e Admins",
            description = """
                    Retorna lista de usuários que possuem role MANAGER ou ADMIN.
                    
                    **Casos de Uso:**
                    - Atribuir manager a um colaborador
                    - Visualizar hierarquia de gestão
                    - Relatórios administrativos
                    - Listar aprovadores de férias
                    
                    **Permissões:**
                    - **MANAGER**: Pode listar managers e admins
                    - **ADMIN**: Pode listar managers e admins
                    - **USER**: Não tem acesso (403 Forbidden)
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de managers e admins retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou token inválido/expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão (apenas MANAGER e ADMIN)")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<List<UserResponseDTO>> getManagersAndAdmins();

    @Operation(
            summary = "Remover colaborador (ADMIN)",
            description = """
                    Remove um colaborador do sistema.
                    Operação exclusiva para ADMIN.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Apenas ADMIN pode remover usuários"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (usuário)")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    ResponseEntity<Void> deleteUser(@PathVariable("id") UUID userId);
}
