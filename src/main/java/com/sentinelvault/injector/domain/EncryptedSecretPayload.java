package com.sentinelvault.injector.domain;

/**
 * Representa o payload cifrado individual vindo da API do Sentinel Vault.
 *
 * @param key A chave do segredo (ex: SPRING_DATASOURCE_PASSWORD)
 * @param cipherText O texto cifrado em AES-256-GCM
 * @param initializationVector O Vetor de Inicialização (IV) em Hex/Base64
 */
public record EncryptedSecretPayload(
        String key,
        String cipherText,
        String initializationVector
) {
    public EncryptedSecretPayload {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Secret key cannot be null or empty");
        }
        if (cipherText == null || cipherText.isBlank()) {
            throw new IllegalArgumentException("Ciphertext cannot be null or empty");
        }
    }
}