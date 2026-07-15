package com.sentinelvault.config;

import com.sentinelvault.security.SimpleTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF (Cross-Site Request Forgery) pois nossa API é Stateless baseada em Tokens [cite: 1294]
                .csrf(csrf -> csrf.disable())

                // Configura a gestão de sessão como STATELESS [cite: 1294]
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Trata erros de autenticação padrão para responder explicitamente com 401 Unauthorized
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                // Configura as regras de autorização de rotas [cite: 1294]
                .authorizeHttpRequests(authorize -> authorize
                        // Endpoints públicos: cadastro de usuário e fluxo de login [cite: 1294]
                        .requestMatchers("/api/users", "/api/auth/login").permitAll()
                        // Qualquer outra rota (incluindo as de credenciais) exige autenticação obrigatória [cite: 1294]
                        .anyRequest().authenticated()
                )

                // Registra nosso filtro personalizado antes do filtro de autenticação padrão do Spring [cite: 1294]
                .addFilterBefore(new SimpleTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}