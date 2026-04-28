package com.mateuslll.taskflow.infrastructure.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    
private String secret = "your-256-bit-secret-key-change-in-production-must-be-at-least-256-bits-long";
    
private Long accessTokenExpiration = 3600000L;
    
private Long refreshTokenExpiration = 604800000L;
    
private String issuer = "taskflow-backend";

public Long getAccessTokenExpirationInSeconds() {
        return accessTokenExpiration / 1000;
    }

public Long getRefreshTokenExpirationInSeconds() {
        return refreshTokenExpiration / 1000;
    }
}
