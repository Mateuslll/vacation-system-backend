package com.mateuslll.taskflow.builders;

import com.mateuslll.taskflow.domain.entities.user.Role;
import com.mateuslll.taskflow.domain.entities.user.User;
import com.mateuslll.taskflow.domain.enums.UserStatus;
import com.mateuslll.taskflow.domain.valueobject.Email;
import com.mateuslll.taskflow.domain.valueobject.Password;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserBuilder {

    private UUID id = null;
    private String email = "test.user@taskflow.com";
    private String rawPassword = "Test@123";
    private String firstName = "Test";
    private String lastName = "User";
    private String department = "Engineering";
    private String position = "Developer";
    private UUID managerId = null;
    private UserStatus status = UserStatus.ACTIVE;
    private Set<Role> roles = new HashSet<>();
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime lastLoginAt = null;

    public static UserBuilder aUser() {
        return new UserBuilder();
    }

    public static User defaultUser() {
        return aUser().build();
    }

    public static User admin() {
        return aUser()
                .withEmail("admin@taskflow.com")
                .withFirstName("Admin")
                .withLastName("System")
                .withPosition("Administrator")
                .withRole(RoleBuilder.adminRole())
                .build();
    }

    public static User manager() {
        return aUser()
                .withEmail("manager@taskflow.com")
                .withFirstName("Manager")
                .withLastName("Team")
                .withPosition("Team Manager")
                .withRole(RoleBuilder.managerRole())
                .build();
    }

    public static User employee() {
        return aUser()
                .withEmail("employee@taskflow.com")
                .withFirstName("Employee")
                .withLastName("Regular")
                .withPosition("Junior Developer")
                .withRole(RoleBuilder.userRole())
                .build();
    }

    public UserBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.rawPassword = password;
        return this;
    }

    public UserBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder withDepartment(String department) {
        this.department = department;
        return this;
    }

    public UserBuilder withPosition(String position) {
        this.position = position;
        return this;
    }

    public UserBuilder withManagerId(UUID managerId) {
        this.managerId = managerId;
        return this;
    }

    public UserBuilder withStatus(UserStatus status) {
        this.status = status;
        return this;
    }

    public UserBuilder withRole(Role role) {
        this.roles.add(role);
        return this;
    }

    public UserBuilder withRoles(Set<Role> roles) {
        this.roles = new HashSet<>(roles);
        return this;
    }

    public UserBuilder inactive() {
        this.status = UserStatus.INACTIVE;
        return this;
    }

    public UserBuilder blocked() {
        this.status = UserStatus.BLOCKED;
        return this;
    }

    public UserBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public UserBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public UserBuilder withLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
        return this;
    }

    public User build() {
        Email emailVO = new Email(email);
        Password passwordVO = Password.fromPlainText(rawPassword);

        UUID userId = (id != null) ? id : UUID.randomUUID();
        return new User(
                userId, emailVO, passwordVO, firstName, lastName,
                department, position, managerId, status, roles,
                createdAt, updatedAt, lastLoginAt
        );
    }
}
