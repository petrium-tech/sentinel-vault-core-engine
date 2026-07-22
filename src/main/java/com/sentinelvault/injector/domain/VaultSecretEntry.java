package com.sentinelvault.injector.domain;

import java.time.Instant;

/**
 * Representa a credencial descriptografada e pronta para inserção em RAM.
 *
 * @param key A chave do ambiente
 * @param value O valor descriptografado
 * @param injectedAt O timestamp de injeção em memória
 */
public record VaultSecretEntry(
        String key,
        String value,
        Instant injectedAt
) {
    public VaultSecretEntry(String key, String value) {
        this(key, value, Instant.now());
    }
}