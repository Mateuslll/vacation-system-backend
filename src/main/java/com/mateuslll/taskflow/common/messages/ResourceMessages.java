package com.mateuslll.taskflow.common.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResourceMessages {

    INVALID_CREDENTIALS("Credenciais inválidas. Verifique email e senha."),
    USER_NOT_ACTIVE("Usuário inativo. Entre em contato com o administrador."),
    USER_BLOCKED("Usuário bloqueado. Entre em contato com o administrador."),
    INVALID_TOKEN("Token inválido ou expirado."),
    INVALID_REFRESH_TOKEN("Token de atualização inválido."),
    AUTHENTICATION_SUCCESSFUL("Autenticação realizada com sucesso."),
    LOGOUT_SUCCESSFUL("Logout realizado com sucesso."),

    USER_NOT_FOUND("Usuário não encontrado."),
    USER_NOT_FOUND_BY_ID("Usuário não encontrado para o id: %s"),
    USER_NOT_FOUND_BY_EMAIL("Usuário não encontrado para o email: %s"),
    EMAIL_ALREADY_REGISTERED("Email já cadastrado no sistema."),
    USER_CREATED_SUCCESSFULLY("Usuário criado com sucesso."),
    USER_UPDATED_SUCCESSFULLY("Usuário atualizado com sucesso."),
    USER_ACTIVATED_SUCCESSFULLY("Usuário ativado com sucesso."),
    USER_DEACTIVATED_SUCCESSFULLY("Usuário desativado com sucesso."),
    USER_DELETED_SUCCESSFULLY("Usuário deletado com sucesso."),
    USER_CANNOT_BE_NULL("Usuário não pode ser nulo."),
    
    PASSWORDS_DO_NOT_MATCH("As senhas não coincidem."),
    CURRENT_PASSWORD_INCORRECT("Senha atual está incorreta."),
    PASSWORD_CHANGED_SUCCESSFULLY("Senha alterada com sucesso."),
    PASSWORD_RESET_SUCCESSFULLY("Senha redefinida com sucesso."),
    PASSWORD_CANNOT_BE_EMPTY("Senha não pode ser vazia."),
    PASSWORD_MINIMUM_LENGTH("Senha deve ter no mínimo 8 caracteres."),
    PASSWORD_MUST_CONTAIN_UPPERCASE("Senha deve conter ao menos uma letra maiúscula."),
    PASSWORD_MUST_CONTAIN_LOWERCASE("Senha deve conter ao menos uma letra minúscula."),
    PASSWORD_MUST_CONTAIN_NUMBER("Senha deve conter ao menos um número."),
    PASSWORD_REQUIREMENTS("Senha deve ter no mínimo 8 caracteres, incluindo letras maiúsculas, minúsculas e números."),
    
    ROLE_NOT_FOUND("Role não encontrado."),
    ROLE_NOT_FOUND_BY_ID("Role não encontrado para o id: %s"),
    ROLE_NOT_FOUND_BY_NAME("Role não encontrado para o nome: %s"),
    ROLE_ALREADY_EXISTS("Role já existe com esse nome."),
    USER_MUST_HAVE_AT_LEAST_ONE_ROLE("Usuário deve ter pelo menos um papel/role."),
    PERMISSION_DENIED("Você não tem permissão para realizar esta ação."),
    INSUFFICIENT_PERMISSIONS("Permissões insuficientes para esta operação."),

    VACATION_REQUEST_NOT_FOUND("Solicitação de férias não encontrada."),
    VACATION_REQUEST_NOT_FOUND_BY_ID("Solicitação de férias não encontrada para o id: %s"),
    VACATION_REQUEST_CREATED_SUCCESSFULLY("Solicitação de férias criada com sucesso."),
    VACATION_REQUEST_UPDATED_SUCCESSFULLY("Solicitação de férias atualizada com sucesso."),
    VACATION_REQUEST_APPROVED_SUCCESSFULLY("Solicitação de férias aprovada com sucesso."),
    VACATION_REQUEST_REJECTED_SUCCESSFULLY("Solicitação de férias rejeitada com sucesso."),
    VACATION_REQUEST_CANCELLED_SUCCESSFULLY("Solicitação de férias cancelada com sucesso."),
    VACATION_REQUEST_DELETED_SUCCESSFULLY("Solicitação de férias deletada com sucesso."),
    
    VACATION_PERIOD_IN_PAST("Período de férias não pode ser no passado."),
    VACATION_PERIOD_MUST_BE_FUTURE("Período de férias deve ser no futuro."),
    VACATION_MINIMUM_DAYS("Período de férias deve ter no mínimo %d dias."),
    VACATION_MAXIMUM_DAYS("Período de férias deve ter no máximo %d dias."),
    VACATION_MINIMUM_ADVANCE("Férias devem ser solicitadas com pelo menos %d dias de antecedência."),
    VACATION_CANNOT_START_WEEKEND("Férias não podem começar no final de semana."),
    VACATION_CANNOT_END_WEEKEND("Férias não podem terminar no final de semana."),
    VACATION_PERIOD_OVERLAP("Período de férias se sobrepõe a uma solicitação existente."),
    VACATION_ALREADY_STARTED("Não é possível modificar férias já iniciadas."),
    VACATION_ALREADY_PROCESSED("Solicitação já foi processada e não pode ser modificada."),
    VACATION_INSUFFICIENT_BALANCE("Saldo insuficiente de férias. Solicitado: %d dias, Disponível: %d dias"),
    VACATION_REASON_TOO_SHORT("Motivo deve ter no mínimo 10 caracteres."),
    VACATION_REASON_TOO_LONG("Motivo deve ter no máximo 500 caracteres."),
    VACATION_REASON_REQUIRED("Motivo é obrigatório."),
    REJECTION_REASON_REQUIRED("Motivo da rejeição é obrigatório."),
    REJECTION_REASON_TOO_SHORT("Motivo da rejeição deve ter no mínimo 10 caracteres."),
    ONLY_USER_CAN_CANCEL("Apenas o próprio usuário pode cancelar sua solicitação."),
    ONLY_MANAGER_CAN_APPROVE("Apenas gerentes podem aprovar solicitações de férias."),
    
    FIELD_REQUIRED("Campo obrigatório: %s"),
    FIELD_INVALID("Campo inválido: %s"),
    FIELD_TOO_SHORT("Campo %s deve ter no mínimo %d caracteres."),
    FIELD_TOO_LONG("Campo %s deve ter no máximo %d caracteres."),
    EMAIL_INVALID("Email inválido."),
    DATE_RANGE_INVALID("Intervalo de datas inválido."),
    START_DATE_AFTER_END_DATE("Data de início não pode ser posterior à data de fim."),
    
    INTERNAL_SERVER_ERROR("Erro interno do servidor. Tente novamente mais tarde."),
    ERROR_PROCESSING_REQUEST("Não foi possível processar a solicitação."),
    SUCCESS_PROCESSING_REQUEST("Solicitação processada com sucesso."),
    RESOURCE_NOT_FOUND("Recurso não encontrado."),
    RESOURCE_ALREADY_EXISTS("Recurso já existe."),
    OPERATION_NOT_ALLOWED("Operação não permitida."),
    INVALID_REQUEST("Requisição inválida."),
    VALIDATION_ERROR("Erro de validação."),
    
    DEPARTMENT_NOT_FOUND("Departamento não encontrado."),
    DEPARTMENT_REQUIRED("Departamento é obrigatório."),
    
    NOTIFICATION_SENT_SUCCESSFULLY("Notificação enviada com sucesso."),
    ERROR_SENDING_NOTIFICATION("Erro ao enviar notificação."),
    
    PAGE_NOT_FOUND("Página não encontrada."),
    INVALID_PAGE_NUMBER("Número de página inválido."),
    INVALID_PAGE_SIZE("Tamanho de página inválido."),
    NO_RESULTS_FOUND("Nenhum resultado encontrado.");

    private final String message;

    public String format(Object... args) {
        return String.format(this.message, args);
    }
}
