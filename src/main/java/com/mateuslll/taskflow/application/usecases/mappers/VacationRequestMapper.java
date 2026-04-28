package com.mateuslll.taskflow.application.usecases.mappers;

import com.mateuslll.taskflow.application.usecases.vacation.VacationRequestResponseDTO;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.entities.vacation.VacationRequest;
import com.mateuslll.taskflow.domain.enums.RoleName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Comparator;

@Mapper(componentModel = "spring")
public interface VacationRequestMapper {

@Mapping(target = "id", source = "vacationRequest.id")
    @Mapping(target = "userId", source = "vacationRequest.userId")
    @Mapping(target = "startDate", expression = "java(vacationRequest.getPeriod().startDate())")
    @Mapping(target = "endDate", expression = "java(vacationRequest.getPeriod().endDate())")
    @Mapping(target = "days", expression = "java(vacationRequest.getPeriod().getDays())")
    @Mapping(target = "reason", source = "vacationRequest.reason")
    @Mapping(target = "status", expression = "java(vacationRequest.getStatus().name())")
    @Mapping(target = "userName", expression = "java(user != null ? user.getFullName() : null)")
    @Mapping(target = "userRole", expression = "java(resolveUserRole(user))")
    @Mapping(target = "approvedBy", source = "vacationRequest.approvedBy")
    @Mapping(target = "approvedByName", expression = "java(manager != null ? manager.getFullName() : null)")
    @Mapping(target = "rejectionReason", source = "vacationRequest.rejectionReason")
    @Mapping(target = "createdAt", source = "vacationRequest.createdAt")
    @Mapping(target = "updatedAt", source = "vacationRequest.updatedAt")
    @Mapping(target = "processedAt", source = "vacationRequest.processedAt")
    VacationRequestResponseDTO toResponse(VacationRequest vacationRequest, User user, User manager);

    default String resolveUserRole(User user) {
        if (user == null || user.getRoles().isEmpty()) {
            return null;
        }

        return user.getRoles().stream()
                .map(role -> role.getName())
                .min(Comparator.comparingInt(this::rolePriority))
                .map(RoleName::name)
                .orElse(null);
    }

    default int rolePriority(RoleName roleName) {
        return switch (roleName) {
            case ADMIN -> 1;
            case MANAGER -> 2;
            case USER -> 3;
        };
    }
}
