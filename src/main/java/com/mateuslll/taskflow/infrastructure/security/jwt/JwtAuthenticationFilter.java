package com.mateuslll.taskflow.infrastructure.security.jwt;

import com.mateuslll.taskflow.infrastructure.security.jwt.token.TokenBlacklistService;
import com.mateuslll.taskflow.infrastructure.security.jwt.token.service.TokenService;
import com.mateuslll.taskflow.infrastructure.security.jwt.token.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String token = extractToken(request);

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (!tokenService.isTokenValid(token, TokenType.ACCESS)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (blacklistService.isTokenBlacklisted(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            tokenService.extractClaims(token).ifPresent(claims -> setAuthentication(claims, request));

        } catch (Exception e) {
            log.error("Erro ao processar autenticação JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        return null;
    }

private void setAuthentication(JwtClaims claims, HttpServletRequest request) {
        String userId = claims.getUserId();
        List<String> roles = claims.getRoles();
        
        List<SimpleGrantedAuthority> authorities = Optional.ofNullable(roles)
                .orElse(Collections.emptyList())
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        JwtClaimsDTO claimsDTO = tokenService.toJwtClaimsDTO(claims.getAllClaims());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userId,
                        claimsDTO,
                        authorities
                );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
