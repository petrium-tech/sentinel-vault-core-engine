package com.sentinelvault.domain.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    // Cache em memória thread-safe para armazenar os buckets de cada IP individualmente
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    /**
     * Recupera ou cria um balde de tokens (Bucket) associado ao endereço IP do cliente.
     */
    public Bucket resolveBucket(String ipAddress) {
        return cache.computeIfAbsent(ipAddress, this::createNewBucket);
    }

    /**
     * Define a política estrita de Throttling.
     * Regra: Permite no máximo 5 requisições por minuto por IP.
     * O balde regenera 5 tokens completos a cada 1 minuto.
     */
    private Bucket createNewBucket(String ipAddress) {
        // Define o limite (Bandwidth): 5 tokens de capacidade, recarregando 5 tokens a cada 1 minuto
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}