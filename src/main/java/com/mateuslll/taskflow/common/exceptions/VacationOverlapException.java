package com.mateuslll.taskflow.common.exceptions;

import com.mateuslll.taskflow.domain.entities.vacation.VacationRequest;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class VacationOverlapException extends BaseException {

    private final String conflictingUserName;
    private final LocalDate conflictingStartDate;
    private final LocalDate conflictingEndDate;
    private final String conflictingVacationId;

public VacationOverlapException(String message, VacationRequest conflictingVacation) {
        super(message);
        this.conflictingUserName = "Usuário";
        this.conflictingStartDate = conflictingVacation.getPeriod().startDate();
        this.conflictingEndDate = conflictingVacation.getPeriod().endDate();
        this.conflictingVacationId = conflictingVacation.getId().toString();
    }

public VacationOverlapException(
            String message,
            String userName,
            LocalDate startDate,
            LocalDate endDate,
            String vacationId) {
        super(message);
        this.conflictingUserName = userName;
        this.conflictingStartDate = startDate;
        this.conflictingEndDate = endDate;
        this.conflictingVacationId = vacationId;
    }

public String getDetailedMessage() {
        return String.format(
            "%s | Conflito com férias de %s (%s a %s) - ID: %s",
            getMessage(),
            conflictingUserName,
            conflictingStartDate,
            conflictingEndDate,
            conflictingVacationId
        );
    }
}
