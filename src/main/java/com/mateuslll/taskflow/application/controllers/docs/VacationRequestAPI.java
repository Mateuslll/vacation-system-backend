package com.mateuslll.taskflow.application.controllers.docs;

import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;
import com.mateuslll.taskflow.application.usecases.vacation.create.dto.CreateVacationRequestRequestDTO;
import com.mateuslll.taskflow.application.usecases.vacation.getapproved.ApprovedVacationPeriodDTO;
import com.mateuslll.taskflow.application.usecases.vacation.reject.RejectVacationRequestRequestDTO;
import com.mateuslll.taskflow.application.usecases.vacation.update.UpdateVacationRequestRequestDTO;
import com.mateuslll.taskflow.domain.enums.RequestStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface VacationRequestAPI {

    @Operation(
            summary = "Criar solicitação de férias",
            description = """
                Cria uma nova solicitação de férias para um usuário com status inicial PENDING:
                - Valida se o usuário existe no sistema
                - Valida permissão por perfil (apenas USER pode solicitar férias)
                - Formato de período obrigatório: `yyyy-MM-dd` (sem hora)
                - Valida período de férias (mínimo 5 dias, máximo 30 dias, deve ser futuro)
                - Valida motivo (mínimo 10 caracteres, máximo 500 caracteres)
                - Verifica se não há sobreposição com outras solicitações aprovadas ou pendentes
                - Retorna a solicitação criada com detalhes completos do usuário

                Regras de Negócio:
                - Usuários com perfil ADMIN não podem solicitar férias
                - Usuários com perfil MANAGER não podem solicitar férias
                - Usuário promovido para MANAGER também fica bloqueado para novas solicitações
                """,
            security = {@SecurityRequirement(name = "Bearer Authentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Solicitação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para solicitar férias (ADMIN/MANAGER)"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (usuário)"),
            @ApiResponse(responseCode = "409", description = "Conflito de estado (sobreposição de período)"),
            @ApiResponse(responseCode = "422", description = "Regra de negócio violada")
    })
    ResponseEntity<VacationRequestResponseDTO> createVacationRequest(
            @Valid @RequestBody CreateVacationRequestRequestDTO request);

    @Operation(
            summary = "Buscar solicitação por ID",
            description = """
                Retorna os detalhes completos de uma solicitação de férias específica:
                - Busca a solicitação pelo identificador único
                - Inclui informações do usuário solicitante
                - Inclui informações do gerente aprovador/rejeitador (quando aplicável)
                - Retorna status atual e histórico de processamento
                """,
            security = {@SecurityRequirement(name = "Bearer Authentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação encontrada"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (solicitação)")
    })
    ResponseEntity<VacationRequestResponseDTO> getVacationRequestById(
            @PathVariable("id") UUID vacationRequestId);

    @Operation(
            summary = "Aprovar solicitação de férias",
            description = """
                Aprova uma solicitação de férias pendente (requer permissão MANAGER ou ADMIN):
                - Valida se a solicitação existe e está em status PENDING
                - Valida se o gerente tem permissão para aprovar
                - Verifica se as férias ainda não iniciaram
                - Atualiza status para APPROVED e registra data de processamento
                - Associa o ID do gerente aprovador
                - Retorna a solicitação atualizada com informações completas
                
                Regras de Negócio:
                - Somente solicitações PENDING podem ser aprovadas
                - Férias já iniciadas não podem ser aprovadas
                - Gerente deve existir no sistema
                """,
            security = {@SecurityRequirement(name = "Bearer Authentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação aprovada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para aprovar"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (solicitação ou gerente)"),
            @ApiResponse(responseCode = "409", description = "Conflito de estado (solicitação já processada ou férias iniciadas)")
    })
    ResponseEntity<VacationRequestResponseDTO> approveVacationRequest(
            @PathVariable("id") UUID vacationRequestId);

    @Operation(
            summary = "Rejeitar solicitação de férias",
            description = """
                Rejeita uma solicitação de férias pendente com motivo obrigatório (requer permissão MANAGER ou ADMIN):
                - Valida se a solicitação existe e está em status PENDING
                - Valida se o gerente tem permissão para rejeitar
                - Exige motivo de rejeição com mínimo de 10 caracteres
                - Atualiza status para REJECTED e registra data de processamento
                - Associa o ID do gerente rejeitador (extraído do JWT) e o motivo da rejeição
                - Retorna a solicitação atualizada com informações completas
                
                Regras de Negócio:
                - Somente solicitações PENDING podem ser rejeitadas
                - Motivo de rejeição é obrigatório (mínimo 10 caracteres)
                - managerId obtido na borda a partir da identidade autenticada
                """,
            security = {@SecurityRequirement(name = "Bearer Authentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação rejeitada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para rejeitar"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (solicitação ou gerente)"),
            @ApiResponse(responseCode = "409", description = "Conflito de estado (solicitação já processada)")
    })
    ResponseEntity<VacationRequestResponseDTO> rejectVacationRequest(
            @PathVariable("id") UUID vacationRequestId,
            @Valid @RequestBody RejectVacationRequestRequestDTO request);

    @Operation(
            summary = "Editar solicitação de férias",
            description = """
                Atualiza período e motivo de uma solicitação de férias.
                Somente solicitações pendentes podem ser editadas.
                O colaborador dono da solicitação pode editar; ADMIN também pode editar.
                """,
            security = {@SecurityRequirement(name = "Bearer Authentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para atualizar a solicitação"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (solicitação ou usuário)"),
            @ApiResponse(responseCode = "409", description = "Conflito de estado (solicitação já processada ou sobreposição)")
    })
    ResponseEntity<VacationRequestResponseDTO> updateVacationRequest(
            @PathVariable("id") UUID vacationRequestId,
            @Valid @RequestBody UpdateVacationRequestRequestDTO request);

    @Operation(
            summary = "Cancelar solicitação de férias",
            description = """
                Cancela uma solicitação de férias pendente (somente o próprio usuário pode cancelar):
                - Valida se a solicitação existe e está em status PENDING
                - Verifica se o usuário solicitante é o dono da solicitação (userId do JWT)
                - Verifica se as férias ainda não iniciaram
                - Atualiza status para CANCELLED e registra data de atualização
                - Retorna a solicitação atualizada
                
                Regras de Negócio:
                - Somente solicitações PENDING podem ser canceladas
                - Apenas o próprio usuário pode cancelar sua solicitação
                - userId obtido na borda a partir da identidade autenticada
                - Férias já iniciadas não podem ser canceladas
                - Solicitações já processadas (APPROVED/REJECTED) não podem ser canceladas
                """,
            security = {@SecurityRequirement(name = "Bearer Authentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação cancelada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para cancelar esta solicitação (não é o dono)"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (solicitação ou usuário)"),
            @ApiResponse(responseCode = "409", description = "Conflito de estado (solicitação já processada ou férias iniciadas)")
    })
    ResponseEntity<VacationRequestResponseDTO> cancelVacationRequest(
            @PathVariable("id") UUID vacationRequestId);

    @Operation(
            summary = "Listar todas as solicitações de férias (ADMIN)",
            description = """
                Lista todas as solicitações de férias do sistema com filtros opcionais (requer ADMIN e MANAGER):
                
                **Filtros disponíveis (todos opcionais):**
                - **status**: Filtrar por RequestStatus (PENDING, APPROVED, REJECTED, CANCELLED)
                - **userId**: Filtrar por ID do usuário
                - **startDate**: Férias que terminam após esta data (`yyyy-MM-dd`, sem hora)
                - **endDate**: Férias que começam antes desta data (`yyyy-MM-dd`, sem hora)
                - **startDate + endDate**: Férias que se sobrepõem ao período
                
                **Características:**
                - Sem filtros: retorna TODAS as férias do sistema
                - Ordenadas por data de solicitação (mais recente primeiro)
                - Inclui informações completas do usuário e aprovador
                """,
            security = {@SecurityRequirement(name = "Bearer Authentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de férias retornada com sucesso (pode estar vazia)"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou token inválido/expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão ADMIN")
    })
    ResponseEntity<List<VacationRequestResponseDTO>> getAllVacationRequests(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate);

    @Operation(
            summary = "Listar minhas solicitações de férias",
            description = """
                Lista todas as solicitações de férias do usuário autenticado (extraído do JWT):
                
                **Características:**
                - Retorna apenas as férias do próprio usuário
                - Ordenadas por data de solicitação (mais recente primeiro)
                - Inclui férias em todos os status: PENDING, APPROVED, REJECTED, CANCELLED
                - Disponível para qualquer usuário autenticado
                
                **Casos de uso:**
                - Usuário visualizar histórico de férias
                - Acompanhar status de solicitações pendentes
                - Verificar férias aprovadas futuras
                - Consultar férias passadas
                """,
            security = {@SecurityRequirement(name = "Bearer Authentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de férias retornada com sucesso (pode estar vazia)"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou token inválido/expirado")
    })
    ResponseEntity<List<VacationRequestResponseDTO>> getMyVacationRequests();

    @Operation(
            summary = "Listar solicitações de férias da equipe (MANAGER/ADMIN)",
            description = """
                Lista solicitações de férias da equipe do usuário autenticado:
                
                **Permissões e Regras:**
                - **MANAGER**: Retorna férias de seus colaboradores diretos (managerId)
                - **ADMIN**: Retorna TODAS as férias do sistema
                - **USER**: Sem permissão (403 Forbidden)
                
                **Casos de uso:**
                - Manager visualizar férias pendentes de aprovação da equipe
                - Manager acompanhar histórico de férias dos colaboradores
                - Admin ter visão geral de todas as férias
                - Dashboard de gestão de férias
                """,
            security = {@SecurityRequirement(name = "Bearer Authentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de férias da equipe retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou token inválido/expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão MANAGER ou ADMIN"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado (usuário)")
    })
    ResponseEntity<List<VacationRequestResponseDTO>> getTeamVacationRequests();

    @Operation(
            summary = "Buscar férias aprovadas (calendário)",
            description = """
                Retorna todos os períodos de férias aprovadas para auxiliar na visualização e gestão.
                **ÚTIL PARA MANAGERS** que precisam visualizar datas já ocupadas antes de aprovar novas solicitações.
                
                **Fluxo:**
                1. Busca todas as VacationRequests com status APPROVED
                2. Aplica filtros opcionais por período
                3. Ordena por data de início
                4. Retorna lista de períodos com informações resumidas do colaborador
                
                **Casos de Uso:**
                - Manager visualizando calendário de férias antes de aprovar nova solicitação
                - Dashboard exibindo períodos ocupados
                - Planejamento de férias (evitar sobreposição de equipe)
                
                **Filtros Disponíveis:**
                - **startDate** (opcional): Retorna férias que terminam APÓS esta data (`yyyy-MM-dd`, sem hora)
                - **endDate** (opcional): Retorna férias que começam ANTES desta data (`yyyy-MM-dd`, sem hora)
                
                **Sem filtros** - Retorna TODAS as férias aprovadas.
                **Permissões:**
                - Qualquer usuário autenticado pode visualizar férias aprovadas
                - Útil principalmente para MANAGERS e ADMINS no processo de aprovação
                """,
            security = {@SecurityRequirement(name = "Bearer Authentication")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de férias aprovadas retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou token inválido/expirado"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    ResponseEntity<List<ApprovedVacationPeriodDTO>> getApprovedVacations(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    );
}
