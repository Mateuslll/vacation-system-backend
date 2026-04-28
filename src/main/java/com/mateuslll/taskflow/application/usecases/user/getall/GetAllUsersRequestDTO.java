package com.mateuslll.taskflow.application.usecases.user.getall;

public record GetAllUsersRequestDTO(
        UserStatusFilter status
) {

    public GetAllUsersRequestDTO() {
        this(null);
    }

    public boolean isAll() {
        return status == UserStatusFilter.ALL;
    }

    public boolean isInactive() {
        return status == UserStatusFilter.INACTIVE;
    }

    public boolean isActiveOrDefault() {
        return status == null || status == UserStatusFilter.ACTIVE;
    }
}
