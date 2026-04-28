package com.mateuslll.taskflow.application.persistence.mappers;

import com.mateuslll.taskflow.application.persistence.entities.vacation.VacationRequestEntity;
import com.mateuslll.taskflow.domain.entities.vacation.VacationRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface VacationRequestPersistenceMapper {

@Mapping(target = "user", ignore = true)
    @Mapping(target = "approvedBy", ignore = true)
    @Mapping(target = "startDate", source = "period.startDate")
    @Mapping(target = "endDate", source = "period.endDate")
    VacationRequestEntity toEntity(VacationRequest domain);
}
