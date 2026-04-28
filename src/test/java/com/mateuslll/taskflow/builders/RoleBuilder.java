package com.mateuslll.taskflow.builders;

import com.mateuslll.taskflow.domain.entities.user.Role;
import com.mateuslll.taskflow.domain.enums.RoleName;

import java.util.Set;
import java.util.UUID;


public class RoleBuilder {

    private UUID id = UUID.randomUUID();
    private RoleName name = RoleName.USER;
    private String description = "User role with basic permissions";
    private Set<String> permissions = null;

    public static RoleBuilder aRole() {
        return new RoleBuilder();
    }

    public static Role userRole() {
        return aRole().withName(RoleName.USER).build();
    }

    public static Role managerRole() {
        return aRole()
                .withName(RoleName.MANAGER)
                .withDescription("Manager role with approval permissions")
                .build();
    }

    public static Role adminRole() {
        return aRole()
                .withName(RoleName.ADMIN)
                .withDescription("Administrator role with full permissions")
                .build();
    }

    public RoleBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public RoleBuilder withName(RoleName name) {
        this.name = name;
        return this;
    }

    public RoleBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public RoleBuilder withPermissions(Set<String> permissions) {
        this.permissions = permissions;
        return this;
    }

    public Role build() {
        if (permissions != null) {
            return new Role(id, name, description, permissions);
        }
        return new Role(name, description);
    }
}
