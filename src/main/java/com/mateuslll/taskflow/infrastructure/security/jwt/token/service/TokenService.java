package com.mateuslll.taskflow.infrastructure.security.jwt.token.service;

import com.mateuslll.taskflow.infrastructure.security.jwt.JwtClaims;
import com.mateuslll.taskflow.infrastructure.security.jwt.JwtClaimsDTO;
import com.mateuslll.taskflow.infrastructure.security.jwt.token.TokenType;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface TokenService {
    
String generateAccessToken(Map<String, Object> claims);
    
String generateRefreshToken(Map<String, Object> claims);
    
String generateToken(Map<String, Object> claims, TokenType type);
    
boolean isTokenValid(String token, TokenType type);
    
Optional<JwtClaims> extractClaims(String token);
    
<T> Optional<T> extractClaim(String token, Function<JwtClaims, T> resolver);
    
JwtClaimsDTO toJwtClaimsDTO(Map<String, Object> claims);
}
