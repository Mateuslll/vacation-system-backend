package com.mateuslll.taskflow.common.exceptions;

public class VacationAlreadyStartedException extends DomainException {

    public VacationAlreadyStartedException() {
        super("Não é possível modificar férias já iniciadas.");
    }

    public VacationAlreadyStartedException(String requestId) {
        super(String.format("Não é possível modificar a solicitação %s pois as férias já iniciaram.", requestId));
    }
}
