package com.mateuslll.taskflow.domain.entities.user;

import com.mateuslll.taskflow.common.exceptions.DomainException;
import com.mateuslll.taskflow.common.messages.ResourceMessages;
import com.mateuslll.taskflow.domain.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Role {

    private UUID id;
    private RoleName name;
    private String description;
    private Set<String> permissions;

public Role(RoleName name, String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.permissions = new HashSet<>();
        initializeDefaultPermissions();
    }

    public void removePermission(String permission) {
        this.permissions.remove(permission);
    }

    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission);
    }

    private void initializeDefaultPermissions() {
        switch (this.name) {
            case USER -> {
                permissions.add("USER_READ");
                permissions.add("USER_UPDATE_OWN");
                permissions.add("VACATION_CREATE");
                permissions.add("VACATION_READ_OWN");
                permissions.add("VACATION_CANCEL_OWN");
            }
            case MANAGER -> {
                permissions.add("USER_READ");
                permissions.add("VACATION_READ");
                permissions.add("VACATION_READ_ALL");
                permissions.add("VACATION_APPROVE");
                permissions.add("VACATION_REJECT");
                permissions.add("VACATION_CANCEL");
            }
            case ADMIN -> {
                permissions.add("USER_CREATE");
                permissions.add("USER_READ");
                permissions.add("USER_UPDATE");
                permissions.add("USER_DELETE");
                permissions.add("USER_ACTIVATE");
                permissions.add("USER_DEACTIVATE");
                
                permissions.add("ROLE_CREATE");
                permissions.add("ROLE_READ");
                permissions.add("ROLE_UPDATE");
                permissions.add("ROLE_DELETE");
                
                permissions.add("VACATION_READ");
                permissions.add("VACATION_READ_ALL");
                permissions.add("VACATION_APPROVE");
                permissions.add("VACATION_REJECT");
                permissions.add("VACATION_CANCEL");
                
                permissions.add("SYSTEM_ADMIN");
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Role{name=%s, permissions=%d}", 
                name, permissions.size());
    }
}
