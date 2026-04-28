package com.mateuslll.taskflow.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "TaskFlow Manager API",
                version = "1.0.0",
                description = """
                        API REST para gerenciamento de férias corporativas.
                        
                        Para ter acesso à API:
                        1. Crie um admin via `POST /bootstrap/create-admin`
                        2. Faça login em `POST /auth/login`
                        3. Autorize com o token (botão **Authorize**)
                        
                        **Roles:** USER | MANAGER | ADMIN 
                        """,
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        )
)
@SecurityScheme(
        name = "Bearer Authentication",
        description = """
                Autenticação JWT
                
                **Obter token:**
                1. Bootstrap: `POST /bootstrap/create-admin`
                2. Login: `POST /auth/login`
                3. Copie o `accessToken` da resposta e utilize no campo abaixo.
                """,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    
    @Value("${server.port:8080}")
    private int serverPort;
    
    @Value("${server.servlet.context-path:/api/v1}")
    private String contextPath;
    
    @Bean
    public OpenAPI customOpenAPI() {
        Server localServer = new Server()
                .url("http://localhost:" + serverPort + contextPath)
                .description("Servidor Local (porta " + serverPort + ")");
        
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Authentication");
        
        return new OpenAPI()
                .servers(List.of(localServer))
                .addSecurityItem(securityRequirement);
    }
}
