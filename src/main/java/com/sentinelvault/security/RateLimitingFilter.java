package com.sentinelvault.security;

import com.sentinelvault.domain.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1) // Executa como o primeiro filtro na entrada da requisição
public class RateLimitingFilter implements Filter {

    private final RateLimitingService rateLimitingService;

    public RateLimitingFilter(RateLimitingService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Isenção para endpoints de infraestrutura e telemetria (Prometheus scraper / Health check)
        String path = httpRequest.getServletPath();
        if (path.startsWith("/actuator")) {
            chain.doFilter(request, response);
            return;
        }

        // Extrai o IP real do cliente, respeitando balanceadores de carga / proxies reversos
        String ipAddress = extractClientIp(httpRequest);

        // Resolve o balde de tokens para este IP específico
        Bucket bucket = rateLimitingService.resolveBucket(ipAddress);

        // Tenta consumir 1 token do balde. Se não houver tokens disponíveis, barra na hora!
        if (!bucket.tryConsume(1)) {
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\": \"Too Many Requests\", \"message\": \"Rate limit exceeded. Try again in a minute.\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
