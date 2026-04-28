package com.mateuslll.taskflow.domain.entities.user;

import com.mateuslll.taskflow.builders.RoleBuilder;
import com.mateuslll.taskflow.builders.UserBuilder;
import com.mateuslll.taskflow.common.exceptions.BadRequestException;
import com.mateuslll.taskflow.common.exceptions.DomainException;
import com.mateuslll.taskflow.common.exceptions.InvalidPasswordException;
import com.mateuslll.taskflow.common.exceptions.UserAlreadyActiveException;
import com.mateuslll.taskflow.common.exceptions.UserBlockedException;
import com.mateuslll.taskflow.domain.enums.RoleName;
import com.mateuslll.taskflow.domain.enums.UserStatus;
import com.mateuslll.taskflow.domain.valueobject.Email;
import com.mateuslll.taskflow.domain.valueobject.Password;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Domínio da Entidade User")
class UserTest {

    @Nested
    @DisplayName("Criação de Usuário")
    class UserCreation {

        @Test
        @DisplayName("Deve criar usuário com dados válidos")
        void shouldCreateUserWithValidData() {
            Email email = new Email("test@taskflow.com");
            Password password = Password.fromPlainText("Test@123");
            
            User user = new User(email, password, "Test", "User");
            
            assertThat(user).isNotNull();
            assertThat(user.getId()).isNotNull();
            assertThat(user.getEmail()).isEqualTo(email);
            assertThat(user.getFirstName()).isEqualTo("Test");
            assertThat(user.getLastName()).isEqualTo("User");
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(user.getCreatedAt()).isNotNull();
            assertThat(user.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Deve lançar exceção para primeiro nome nulo")
        void shouldThrowExceptionForNullFirstName() {
            Email email = new Email("test@taskflow.com");
            Password password = Password.fromPlainText("Test@123");
            
            assertThatThrownBy(() -> new User(email, password, null, "User"))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("obrigatório");
        }

        @Test
        @DisplayName("Deve lançar exceção para nome muito curto")
        void shouldThrowExceptionForTooShortName() {
            Email email = new Email("test@taskflow.com");
            Password password = Password.fromPlainText("Test@123");
            
            assertThatThrownBy(() -> new User(email, password, "A", "User"))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("mínimo");
        }

        @Test
        @DisplayName("Deve remover espaços em branco dos nomes")
        void shouldTrimWhitespaceFromNames() {
            Email email = new Email("test@taskflow.com");
            Password password = Password.fromPlainText("Test@123");
            
            User user = new User(email, password, "  Test  ", "  User  ");
            
            assertThat(user.getFirstName()).isEqualTo("Test");
            assertThat(user.getLastName()).isEqualTo("User");
        }
    }

    @Nested
    @DisplayName("Gerenciamento de Status do Usuário")
    class UserStatusManagement {

        @Test
        @DisplayName("Deve ativar usuário inativo")
        void shouldActivateInactiveUser() {
            User user = UserBuilder.aUser().inactive().build();
            
            user.activate();
            
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("Deve lançar exceção ao ativar usuário já ativo")
        void shouldThrowExceptionWhenActivatingActiveUser() {
            User user = UserBuilder.aUser().build();
            
            assertThatThrownBy(user::activate)
                    .isInstanceOf(UserAlreadyActiveException.class);
        }

        @Test
        @DisplayName("Deve lançar exceção ao ativar usuário bloqueado")
        void shouldThrowExceptionWhenActivatingBlockedUser() {
            User user = UserBuilder.aUser().blocked().build();
            
            assertThatThrownBy(user::activate)
                    .isInstanceOf(UserBlockedException.class);
        }

        @Test
        @DisplayName("Deve desativar usuário ativo")
        void shouldDeactivateActiveUser() {
            User user = UserBuilder.aUser().build();
            
            user.deactivate();
            
            assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        }

        @Test
        @DisplayName("Deve bloquear usuário")
        void shouldBlockUser() {
            User user = UserBuilder.aUser().build();
            
            user.block();
            
            assertThat(user.getStatus()).isEqualTo(UserStatus.BLOCKED);
        }
    }

    @Nested
    @DisplayName("Gerenciamento de Roles do Usuário")
    class UserRoleManagement {

        @Test
        @DisplayName("Deve adicionar role ao usuário")
        void shouldAddRoleToUser() {
            User user = UserBuilder.aUser().build();
            Role adminRole = RoleBuilder.adminRole();
            
            user.addRole(adminRole);
            
            assertThat(user.getRoles()).contains(adminRole);
            assertThat(user.hasRole(RoleName.ADMIN.name())).isTrue();
        }

        @Test
        @DisplayName("Deve remover role do usuário")
        void shouldRemoveRoleFromUser() {
            Role userRole = RoleBuilder.userRole();
            User user = UserBuilder.aUser().withRole(userRole).build();
            
            user.removeRole(userRole);
            
            assertThat(user.getRoles()).doesNotContain(userRole);
        }

        @Test
        @DisplayName("Deve lançar exceção ao adicionar role nula")
        void shouldThrowExceptionWhenAddingNullRole() {
            User user = UserBuilder.aUser().build();
            
            assertThatThrownBy(() -> user.addRole(null))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Role");
        }

        @Test
        @DisplayName("Deve verificar se usuário tem permissão específica")
        void shouldCheckIfUserHasSpecificPermission() {
            User user = UserBuilder.aUser()
                    .withRole(RoleBuilder.adminRole())
                    .build();
            
            assertThat(user.hasPermission("USER_CREATE")).isTrue();
            assertThat(user.hasPermission("NONEXISTENT_PERMISSION")).isFalse();
        }
    }

    @Nested
    @DisplayName("Atribuição de Manager ao Usuário")
    class UserManagerAssignment {

        @Test
        @DisplayName("Deve atribuir manager ao usuário")
        void shouldAssignManagerToUser() {
            User user = UserBuilder.aUser().build();
            UUID managerId = UUID.randomUUID();
            
            user.assignManager(managerId);
            
            assertThat(user.getManagerId()).isEqualTo(managerId);
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário tenta ser seu próprio manager")
        void shouldThrowExceptionWhenUserTriesToBeTheirOwnManager() {
            User user = UserBuilder.aUser().build();
            
            assertThatThrownBy(() -> user.assignManager(user.getId()))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("manager de si mesmo");
        }

        @Test
        @DisplayName("Deve remover manager do usuário")
        void shouldRemoveManagerFromUser() {
            User user = UserBuilder.aUser().withManagerId(UUID.randomUUID()).build();
            
            user.removeManager();
            
            assertThat(user.getManagerId()).isNull();
        }

        @Test
        @DisplayName("Deve verificar se usuário é gerenciado por manager específico")
        void shouldCheckIfUserIsManagedBySpecificManager() {
            UUID managerId = UUID.randomUUID();
            User user = UserBuilder.aUser().withManagerId(managerId).build();
            
            assertThat(user.isManagedBy(managerId)).isTrue();
            assertThat(user.isManagedBy(UUID.randomUUID())).isFalse();
        }
    }

    @Nested
    @DisplayName("Login do Usuário")
    class UserLogin {

        @Test
        @DisplayName("Deve registrar login para usuário ativo")
        void shouldRegisterLoginForActiveUser() {
            User user = UserBuilder.aUser().build();
            
            assertDoesNotThrow(user::registerLogin);
            assertThat(user.getLastLoginAt()).isNotNull();
        }

        @Test
        @DisplayName("Deve lançar exceção ao registrar login para usuário inativo")
        void shouldThrowExceptionWhenRegisteringLoginForInactiveUser() {
            User user = UserBuilder.aUser().inactive().build();
            
            assertThatThrownBy(user::registerLogin)
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("ativo");
        }

        @Test
        @DisplayName("Deve verificar se usuário pode fazer login")
        void shouldCheckIfUserCanLogin() {
            User activeUser = UserBuilder.aUser().build();
            User inactiveUser = UserBuilder.aUser().inactive().build();
            User blockedUser = UserBuilder.aUser().blocked().build();
            
            assertThat(activeUser.canLogin()).isTrue();
            assertThat(inactiveUser.canLogin()).isFalse();
            assertThat(blockedUser.canLogin()).isFalse();
        }
    }

    @Nested
    @DisplayName("Política de férias")
    class VacationPolicy {

        @Test
        @DisplayName("ensureHasManagerAssigned falha sem gestor")
        void ensureHasManagerAssignedFailsWhenNoManager() {
            User user = UserBuilder.aUser().withRole(RoleBuilder.userRole()).build();
            assertThatThrownBy(user::ensureHasManagerAssigned)
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("gestor");
        }

        @Test
        @DisplayName("ensureHasManagerAssigned passa com gestor")
        void ensureHasManagerAssignedSucceedsWithManager() {
            User user = UserBuilder.aUser()
                    .withRole(RoleBuilder.userRole())
                    .withManagerId(UUID.randomUUID())
                    .build();
            assertDoesNotThrow(user::ensureHasManagerAssigned);
        }
    }
}
