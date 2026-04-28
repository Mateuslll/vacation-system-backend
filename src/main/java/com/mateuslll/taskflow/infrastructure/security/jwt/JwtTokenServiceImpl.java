package com.mateuslll.taskflow.infrastructure.security.jwt;

import com.mateuslll.taskflow.common.exceptions.InvalidTokenException;
import com.mateuslll.taskflow.common.exceptions.TokenExpiredException;
import com.mateuslll.taskflow.infrastructure.security.jwt.token.service.TokenService;
import com.mateuslll.taskflow.infrastructure.security.jwt.token.TokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements TokenService {

    private final JwtProperties jwtProperties;

private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateAccessToken(Map<String, Object> claims) {
        return generateToken(claims, TokenType.ACCESS);
    }

    @Override
    public String generateRefreshToken(Map<String, Object> claims) {
        return generateToken(claims, TokenType.REFRESH);
    }

    @Override
    public String generateToken(Map<String, Object> claims, TokenType type) {
        Date now = new Date();
        Date expiration = calculateExpiration(now, type);
        
        String subject = (String) claims.getOrDefault("userId", claims.get("sub"));

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiration)
                .id(UUID.randomUUID().toString())
                .claim("type", type.name())
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, TokenType type) {
        try {
            Optional<JwtClaims> claimsOpt = extractClaims(token);
            
            if (claimsOpt.isEmpty()) {
                return false;
            }
            
            JwtClaims jwtClaims = claimsOpt.get();
            
            if (jwtClaims.isExpired()) {
                return false;
            }

            String tokenType = jwtClaims.get("type", String.class);
            if (tokenType == null || !tokenType.equals(type.name())) {
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("Erro ao validar token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<JwtClaims> extractClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            return Optional.of(JwtClaims.builder()
                    .claims(claims)
                    .build());
            
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
            
        } catch (MalformedJwtException e) {
            log.error("Token malformado: {}", e.getMessage());
            throw InvalidTokenException.malformed();
            
        } catch (SignatureException e) {
            log.error("Assinatura inválida: {}", e.getMessage());
            throw InvalidTokenException.invalidSignature();
            
        } catch (UnsupportedJwtException e) {
            log.error("Token não suportado: {}", e.getMessage());
            throw InvalidTokenException.unsupportedToken();
            
        } catch (Exception e) {
            log.error("Erro ao extrair claims: {}", e.getMessage());
            throw new InvalidTokenException("Erro ao processar token", e);
        }
    }

    @Override
    public <T> Optional<T> extractClaim(String token, Function<JwtClaims, T> resolver) {
        return extractClaims(token).map(resolver);
    }

    @Override
    public JwtClaimsDTO toJwtClaimsDTO(Map<String, Object> claims) {
        return JwtClaimsDTO.builder()
                .userId((String) claims.get("userId"))
                .email((String) claims.get("email"))
                .name((String) claims.get("name"))
                .roles(getListClaim(claims, "roles"))
                .rules(getListClaim(claims, "rules"))
                .additionalClaims(filterAdditionalClaims(claims))
                .build();
    }

private Date calculateExpiration(Date now, TokenType type) {
        long expiration = switch (type) {
            case ACCESS -> jwtProperties.getAccessTokenExpiration();
            case REFRESH -> jwtProperties.getRefreshTokenExpiration();
        };
        
        return new Date(now.getTime() + expiration);
    }

@SuppressWarnings("unchecked")
    private List<String> getListClaim(Map<String, Object> claims, String key) {
        Object value = claims.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return Collections.emptyList();
    }

private Map<String, Object> filterAdditionalClaims(Map<String, Object> claims) {
        Set<String> standardClaims = Set.of(
                "userId", "email", "name", "roles", "rules",
                "sub", "iss", "iat", "exp", "jti", "type"
        );
        
        Map<String, Object> filtered = new HashMap<>();
        claims.forEach((key, value) -> {
            if (!standardClaims.contains(key)) {
                filtered.put(key, value);
            }
        });
        
        return filtered;
    }
}
