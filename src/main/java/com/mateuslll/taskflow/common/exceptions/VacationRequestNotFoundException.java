package com.mateuslll.taskflow.common.exceptions;


public class VacationRequestNotFoundException extends ResourceNotFoundException {

    public VacationRequestNotFoundException(String requestId) {
        super(String.format("Solicitação de férias não encontrada para o id: %s", requestId));
    }
}
