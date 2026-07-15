package com.sentinelvault.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class SimpleTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extrai o cabeçalho 'Authorization' da requisição
        String authorizationHeader = request.getHeader("Authorization");

        // 2. Verifica se o cabeçalho está presente e começa com o tipo padrão "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // Remove a palavra "Bearer " para isolar o valor do token

            // 3. Valida se o token simulado possui o prefixo que geramos no UserService
            if (token.startsWith("mock-jwt-token-for-")) {
                String email = token.replace("mock-jwt-token-for-", "");

                // 4. Cria um objeto de autenticação fictício para preencher o contexto do Spring Security
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email, null, Collections.emptyList()
                );

                // 5. Autentica o fluxo da requisição corrente no Spring Security
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Segue para o próximo filtro na cadeia do Spring Security
        filterChain.doFilter(request, response);
    }
}