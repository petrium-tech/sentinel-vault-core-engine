package com.sentinelvault.injector.core;

import com.sentinelvault.injector.domain.InjectedPropertyBatch;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Gerenciador responsável por injetar o mapa de propriedades voláteis
 * diretamente na memória RAM do ecossistema Spring Boot com prioridade máxima.
 */
public class VolatileMapManager {

    public static final String PROPERTY_SOURCE_NAME = "sentinelEnvInjectorSource";

    // Blacklist de chaves do sistema operacional que NUNCA podem ser sobrescritas
    private static final Set<String> FORBIDDEN_KEYS = Set.of(
            "JAVA_HOME",
            "PATH",
            "USER",
            "SHELL",
            "SPRING_PROFILES_ACTIVE"
    );

    /**
     * Injeta o lote de propriedades no topo do mapa do Spring (addFirst).
     *
     * @param environment O ambiente configurável do Spring
     * @param batch O lote de credenciais recuperadas do Vault
     */
    public void injectIntoEnvironment(ConfigurableEnvironment environment, InjectedPropertyBatch batch) {
        if (environment == null) {
            throw new SentinelEnvInjectionException("Spring ConfigurableEnvironment cannot be null during RAM injection");
        }
        if (batch == null || batch.entries().isEmpty()) {
            throw new SentinelEnvInjectionException("Cannot inject empty or null property batch into RAM");
        }

        Map<String, Object> filteredProperties = new HashMap<>();

        for (var entry : batch.entries()) {
            String key = entry.key();
            if (isAllowedKey(key)) {
                filteredProperties.put(key, entry.value());
            }
        }

        if (filteredProperties.isEmpty()) {
            throw new SentinelEnvInjectionException("No valid secrets remaining after applying security key filtering");
        }

        MutablePropertySources propertySources = environment.getPropertySources();
        MapPropertySource volatilePropertySource = new MapPropertySource(PROPERTY_SOURCE_NAME, filteredProperties);

        // Insere na PRIMEIRA posição da hierarquia (Precedência Máxima na RAM)
        propertySources.addFirst(volatilePropertySource);
    }

    private boolean isAllowedKey(String key) {
        return key != null && !key.isBlank() && !FORBIDDEN_KEYS.contains(key.toUpperCase());
    }
}