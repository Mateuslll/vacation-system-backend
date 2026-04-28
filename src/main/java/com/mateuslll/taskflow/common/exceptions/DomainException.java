package com.mateuslll.taskflow.common.exceptions;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DomainException extends BaseException {

    private final List<String> details;

    public DomainException(String message) {
        super(message, "domain_rule_violation");
        this.details = new ArrayList<>();
    }

    public DomainException(String message, List<String> details) {
        super(message, "domain_rule_violation");
        this.details = new ArrayList<>(details);
    }
}
