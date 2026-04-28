package com.mateuslll.taskflow.common.exceptions;


public class VacationAlreadyProcessedException extends DomainException {

    public VacationAlreadyProcessedException() {
        super("Solicitação já foi processada e não pode ser modificada.");
    }

    public VacationAlreadyProcessedException(String requestId) {
        super(String.format("Solicitação %s já foi processada e não pode ser modificada.", requestId));
    }
}
