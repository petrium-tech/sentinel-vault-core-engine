package com.sentinelvault.injector.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Agrupador em lote das credenciais para injeção no PropertySource do Spring.
 */
public record InjectedPropertyBatch(
        String sourceName,
        List<VaultSecretEntry> entries
) {
    public Map<String, Object> toPropertyMap() {
        return entries.stream()
                .collect(Collectors.toMap(VaultSecretEntry::key, VaultSecretEntry::value));
    }
}