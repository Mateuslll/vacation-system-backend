package com.mateuslll.taskflow.domain.entities.user;

import com.mateuslll.taskflow.common.exceptions.BadRequestException;
import com.mateuslll.taskflow.common.exceptions.DomainException;
import com.mateuslll.taskflow.common.exceptions.ForbiddenAccessException;
import com.mateuslll.taskflow.common.exceptions.InvalidPasswordException;
import com.mateuslll.taskflow.common.exceptions.UserAlreadyActiveException;
import com.mateuslll.taskflow.common.exceptions.UserAlreadyInactiveException;
import com.mateuslll.taskflow.common.exceptions.UserBlockedException;
import com.mateuslll.taskflow.common.messages.ResourceMessages;
import com.mateuslll.taskflow.domain.enums.RoleName;
import com.mateuslll.taskflow.domain.enums.UserStatus;
import com.mateuslll.taskflow.domain.valueobject.Email;
import com.mateuslll.taskflow.domain.valueobject.Password;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id")
public class User {

    private UUID id;
    private Email email;
    private Password password;
    private String firstName;
    private String lastName;
    private String department;
    private String position;
    private UUID managerId;
    private UserStatus status;
    private Set<Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;

public User(Email email, Password password, String firstName, String lastName) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.password = password;
        this.firstName = validateName(firstName, "Nome");
        this.lastName = validateName(lastName, "Sobrenome");
        this.status = UserStatus.ACTIVE;
        this.roles = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

public User(UUID id, Email email, Password password, String firstName, String lastName,
                String department, String position, UUID managerId, UserStatus status, Set<Role> roles,
                LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLoginAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.position = position;
        this.managerId = managerId;
        this.status = status;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastLoginAt = lastLoginAt;
    }

    public void activate() {
        if (this.status == UserStatus.BLOCKED) {
            throw new UserBlockedException(this.id.toString());
        }
        if (this.status == UserStatus.ACTIVE) {
            throw new UserAlreadyActiveException(this.id.toString());
        }
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        if (this.status == UserStatus.BLOCKED) {
            throw new UserBlockedException(this.id.toString());
        }
        if (this.status == UserStatus.INACTIVE) {
            throw new UserAlreadyInactiveException(this.id.toString());
        }
        this.status = UserStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void block() {
        this.status = UserStatus.BLOCKED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProfile(String firstName, String lastName, String department, String position) {
        if (firstName != null && !firstName.isBlank()) {
            this.firstName = validateName(firstName, "Nome");
        }
        if (lastName != null && !lastName.isBlank()) {
            this.lastName = validateName(lastName, "Sobrenome");
        }
        this.department = department;
        this.position = position;
        this.updatedAt = LocalDateTime.now();
    }

    public void addRole(Role role) {
        if (role == null) {
            throw new DomainException(ResourceMessages.FIELD_REQUIRED.format("Role"));
        }
        this.roles.add(role);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        this.updatedAt = LocalDateTime.now();
    }

    public void assignManager(UUID managerId) {
        if (managerId != null && managerId.equals(this.id)) {
            throw new DomainException("Usuário não pode ser manager de si mesmo");
        }
        this.managerId = managerId;
        this.updatedAt = LocalDateTime.now();
    }

    public void removeManager() {
        this.managerId = null;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isManagedBy(UUID managerId) {
        return this.managerId != null && this.managerId.equals(managerId);
    }

    public void registerLogin() {
        if (!this.status.canLogin()) {
            throw new DomainException(ResourceMessages.USER_NOT_ACTIVE.getMessage());
        }
        this.lastLoginAt = LocalDateTime.now();
    }

    public boolean hasRole(String roleName) {
        return this.roles.stream()
                .anyMatch(role -> role.getName().name().equals(roleName));
    }

    public boolean hasPermission(String permission) {
        return this.roles.stream()
                .anyMatch(role -> role.hasPermission(permission));
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean canLogin() {
        return this.status.canLogin();
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public void ensureCanRequestVacation() {
        boolean isAdmin = this.roles.stream().anyMatch(role -> role.getName() == RoleName.ADMIN);
        if (isAdmin) {
            throw new ForbiddenAccessException("Usuários com perfil ADMIN não podem solicitar férias");
        }
        boolean isManager = this.roles.stream().anyMatch(role -> role.getName() == RoleName.MANAGER);
        if (isManager) {
            throw new ForbiddenAccessException("Usuários com perfil MANAGER não podem solicitar férias");
        }
    }

    public void ensureHasManagerAssigned() {
        if (this.managerId == null) {
            throw new BadRequestException(
                    "Colaborador deve ter um gestor associado antes de solicitar férias"
            );
        }
    }

    private String validateName(String name, String fieldName) {
        if (name == null || name.isBlank()) {
            throw new DomainException(ResourceMessages.FIELD_REQUIRED.format(fieldName));
        }
        if (name.length() < 2) {
            throw new DomainException(ResourceMessages.FIELD_TOO_SHORT.format(fieldName, 2));
        }
        if (name.length() > 50) {
            throw new DomainException(ResourceMessages.FIELD_TOO_LONG.format(fieldName, 50));
        }
        return name.trim();
    }

    @Override
    public String toString() {
        return String.format("User{id=%s, email=%s, name=%s, status=%s, roles=%d}",
                id, email, getFullName(), status, roles.size());
    }
}
