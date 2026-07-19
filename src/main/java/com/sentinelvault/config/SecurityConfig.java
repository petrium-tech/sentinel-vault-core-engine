package com.sentinelvault.config;

import com.sentinelvault.security.JwtService;
import com.sentinelvault.security.SimpleTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;
    private final JwtDecoder jwtDecoder;

    // Injeção limpa via construtor do JwtService e do JwtDecoder assíncrono RSA
    public SecurityConfig(JwtService jwtService, JwtDecoder jwtDecoder) {
        this.jwtService = jwtService;
        this.jwtDecoder = jwtDecoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Defesa de Isolamento: Esta cadeia ignora requisições de protocolo do Auth Server
                .securityMatcher(request -> !request.getServletPath().startsWith("/oauth2/")
                        && !request.getServletPath().startsWith("/connect/")
                        && !request.getServletPath().startsWith("/.well-known/"))

                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                new MediaTypeRequestMatcher(MediaType.APPLICATION_JSON)
                        )
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/users", "/api/auth/login", "/login").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )

                // Ativa o suporte nativo a Resource Server para validar tokens via Chave Pública RSA
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder)))

                .formLogin(Customizer.withDefaults())

                .addFilterBefore(new SimpleTokenFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}