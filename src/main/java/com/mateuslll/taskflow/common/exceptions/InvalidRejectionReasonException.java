package com.mateuslll.taskflow.common.exceptions;


public class InvalidRejectionReasonException extends BadRequestException {

    public InvalidRejectionReasonException() {
        super("Motivo de rejeição é obrigatório e deve ter no mínimo 10 caracteres.");
    }

    public InvalidRejectionReasonException(String message) {
        super(message);
    }
}
