package com.mateuslll.taskflow.domain.valueobject;

import com.mateuslll.taskflow.common.exceptions.InvalidPasswordException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Value Object Password")
class PasswordTest {

    @Nested
    @DisplayName("Criação de Senha Válida")
    class ValidPasswordCreation {

        @Test
        @DisplayName("Deve criar senha com todos os requisitos atendidos")
        void shouldCreatePasswordWithAllRequirementsMet() {
            String validPassword = "Test@123";
            
            Password password = Password.fromPlainText(validPassword);
            
            assertThat(password).isNotNull();
            assertThat(password.hashedValue()).isNotBlank();
            assertThat(password.hashedValue()).isNotEqualTo(validPassword);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "Password@123",
                "Admin@2024",
                "Test#Pass1",
                "SecureP@ssw0rd",
                "MyP@ssw0rd"
        })
        @DisplayName("Deve aceitar vários formatos válidos de senha")
        void shouldAcceptVariousValidFormats(String validPassword) {
            assertDoesNotThrow(() -> Password.fromPlainText(validPassword));
        }

        @Test
        @DisplayName("Deve fazer hash da senha (não armazenar texto plano)")
        void shouldHashPassword() {
            String plainPassword = "Test@123";
            
            Password password = Password.fromPlainText(plainPassword);
            
            assertThat(password.hashedValue()).isNotEqualTo(plainPassword);
            assertThat(password.hashedValue()).hasSizeGreaterThan(plainPassword.length());
        }
    }

    @Nested
    @DisplayName("Regras de Validação de Senha")
    class PasswordValidationRules {

        @Test
        @DisplayName("Deve lançar exceção para senha nula")
        void shouldThrowExceptionForNullPassword() {
            assertThatThrownBy(() -> Password.fromPlainText(null))
                    .isInstanceOf(InvalidPasswordException.class)
                    .hasMessageContaining("Senha");
        }

        @Test
        @DisplayName("Deve lançar exceção para senha vazia")
        void shouldThrowExceptionForEmptyPassword() {
            assertThatThrownBy(() -> Password.fromPlainText(""))
                    .isInstanceOf(InvalidPasswordException.class)
                    .hasMessageContaining("Senha");
        }

        @Test
        @DisplayName("Deve lançar exceção para senha muito curta")
        void shouldThrowExceptionForPasswordTooShort() {
            assertThatThrownBy(() -> Password.fromPlainText("Test@1"))
                    .isInstanceOf(InvalidPasswordException.class)
                    .hasMessageContaining("Senha");
        }

        @Test
        @DisplayName("Deve lançar exceção para senha sem letra maiúscula")
        void shouldThrowExceptionForPasswordWithoutUppercase() {
            assertThatThrownBy(() -> Password.fromPlainText("test@123"))
                    .isInstanceOf(InvalidPasswordException.class)
                    .hasMessageContaining("Senha");
        }

        @Test
        @DisplayName("Deve lançar exceção para senha sem letra minúscula")
        void shouldThrowExceptionForPasswordWithoutLowercase() {
            assertThatThrownBy(() -> Password.fromPlainText("TEST@123"))
                    .isInstanceOf(InvalidPasswordException.class)
                    .hasMessageContaining("Senha");
        }

        @Test
        @DisplayName("Deve lançar exceção para senha sem número")
        void shouldThrowExceptionForPasswordWithoutNumber() {
            assertThatThrownBy(() -> Password.fromPlainText("Test@Pass"))
                    .isInstanceOf(InvalidPasswordException.class)
                    .hasMessageContaining("Senha");
        }

        @Test
        @DisplayName("Deve lançar exceção para senha sem caractere especial")
        void shouldThrowExceptionForPasswordWithoutSpecialCharacter() {
            assertDoesNotThrow(() -> Password.fromPlainText("Test1234"));
        }
    }

    @Nested
    @DisplayName("Verificação de Senha")
    class PasswordMatching {

        @Test
        @DisplayName("Deve fazer match com a mesma senha")
        void shouldMatchSamePassword() {
            String plainPassword = "Test@123";
            Password password = Password.fromPlainText(plainPassword);
            
            assertTrue(password.matches(plainPassword));
        }

        @Test
        @DisplayName("Não deve fazer match com senha diferente")
        void shouldNotMatchDifferentPassword() {
            Password password = Password.fromPlainText("Test@123");
            
            assertFalse(password.matches("Different@456"));
        }

        @Test
        @DisplayName("Não deve fazer match com senha case diferente")
        void shouldNotMatchPasswordWithDifferentCase() {
            Password password = Password.fromPlainText("Test@123");
            
            assertFalse(password.matches("test@123"));
        }
    }
}
