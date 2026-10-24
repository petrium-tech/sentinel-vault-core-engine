package com.sentinelvault.domain.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class RateLimitingService {

    // Proteção contra DoS e vazamento de memória Heap: Máximo de 10.000 IPs retidos em cache LRU
    private static final int MAX_CACHE_SIZE = 10_000;

    private final Map<String, Bucket> cache = Collections.synchronizedMap(
            new LinkedHashMap<>(128, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Bucket> eldest) {
                    return size() > MAX_CACHE_SIZE;
                }
            }
    );

    /**
     * Recupera ou cria um balde de tokens (Bucket) associado ao endereço IP do cliente.
     */
    public Bucket resolveBucket(String ipAddress) {
        return cache.computeIfAbsent(ipAddress, this::createNewBucket);
    }

    /**
     * Define a política de Throttling corporativa:
     * 60 requisições por minuto por IP (1 req/segundo médio) com recarga periódica de 60 tokens.
     */
    private Bucket createNewBucket(String ipAddress) {
        Bandwidth limit = Bandwidth.builder()
                .capacity(60)
                .refillIntervally(60, Duration.ofMinutes(1))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
