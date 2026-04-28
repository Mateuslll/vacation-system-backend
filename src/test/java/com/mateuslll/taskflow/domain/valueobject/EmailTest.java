package com.mateuslll.taskflow.domain.valueobject;

import com.mateuslll.taskflow.common.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Value Object Email")
class EmailTest {

    @Nested
    @DisplayName("Criação de Email Válido")
    class ValidEmailCreation {

        @Test
        @DisplayName("Deve criar email com formato simples válido")
        void shouldCreateEmailWithValidSimpleFormat() {
            String validEmail = "user@example.com";
            
            Email email = new Email(validEmail);
            
            assertThat(email).isNotNull();
            assertThat(email.value()).isEqualTo(validEmail.toLowerCase());
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "test@taskflow.com",
                "admin@company.com",
                "user.name@domain.co.uk",
                "first.last+tag@example.com",
                "a@b.com"
        })
        @DisplayName("Deve aceitar vários formatos válidos de email")
        void shouldAcceptVariousValidFormats(String validEmail) {
            assertDoesNotThrow(() -> new Email(validEmail));
        }

        @Test
        @DisplayName("Deve normalizar email para minúsculas")
        void shouldNormalizeToLowercase() {
            Email email = new Email("USER@TASKFLOW.COM");
            
            assertThat(email.value()).isEqualTo("user@taskflow.com");
        }
    }

    @Nested
    @DisplayName("Validação de Email Inválido")
    class InvalidEmailValidation {

        @Test
        @DisplayName("Deve lançar exceção para email nulo")
        void shouldThrowExceptionForNullEmail() {
            assertThatThrownBy(() -> new Email(null))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Email");
        }

        @Test
        @DisplayName("Deve lançar exceção para email vazio")
        void shouldThrowExceptionForEmptyEmail() {
            assertThatThrownBy(() -> new Email(""))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Email");
        }

        @Test
        @DisplayName("Deve lançar exceção para email em branco")
        void shouldThrowExceptionForBlankEmail() {
            assertThatThrownBy(() -> new Email("   "))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Email");
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "invalid",
                "invalid@",
                "@invalid.com",
                "invalid@.com"
        })
        @DisplayName("Deve rejeitar formatos inválidos de email")
        void shouldRejectInvalidFormats(String invalidEmail) {
            assertThatThrownBy(() -> new Email(invalidEmail))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Email inválido");
        }
    }

    @Nested
    @DisplayName("Igualdade de Email")
    class EmailEquality {

        @Test
        @DisplayName("Deve considerar emails iguais ignorando case")
        void shouldConsiderEmailsEqualIgnoringCase() {
            Email email1 = new Email("user@taskflow.com");
            Email email2 = new Email("USER@TASKFLOW.COM");
            
            assertThat(email1).isEqualTo(email2);
            assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
        }

        @Test
        @DisplayName("Deve considerar emails diferentes como não iguais")
        void shouldConsiderDifferentEmailsNotEqual() {
            Email email1 = new Email("user1@taskflow.com");
            Email email2 = new Email("user2@taskflow.com");
            
            assertThat(email1).isNotEqualTo(email2);
        }
    }
}
