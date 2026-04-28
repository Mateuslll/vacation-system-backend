package com.mateuslll.taskflow.common.exceptions;

public class VacationCancellationNotAllowedException extends ForbiddenException {

    public VacationCancellationNotAllowedException() {
        super("Apenas o próprio usuário pode cancelar sua solicitação de férias.");
    }

    public VacationCancellationNotAllowedException(String userId, String requestId) {
        super(String.format("Usuário %s não tem permissão para cancelar a solicitação %s.", userId, requestId));
    }
}
