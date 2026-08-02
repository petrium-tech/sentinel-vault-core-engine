package com.sentinelvault.injector.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sentinelvault.injector.core.SentinelEnvInjectionException;
import com.sentinelvault.injector.domain.EncryptedSecretPayload;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Cliente HTTP leve e resiliente em Java 21 nativo para comunicação
 * síncrona de alta performance com o Sentinel Vault Core Engine.
 */
public class VaultHttpClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Duration timeout;

    public VaultHttpClient() {
        this(Duration.ofSeconds(3));
    }

    public VaultHttpClient(Duration timeout) {
        this.timeout = timeout;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public VaultHttpClient(HttpClient httpClient, ObjectMapper objectMapper, Duration timeout) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.timeout = timeout;
    }

    /**
     * Consome o endpoint batch do Vault e retorna a lista de payloads cifrados.
     *
     * @param vaultUrl URL base do Vault Core Engine (ex: http://localhost:9090)
     * @param environmentNome Nome do ambiente do ecossistema (ex: DEVELOPMENT)
     * @return Lista de payloads cifrados
     */
    public List<EncryptedSecretPayload> fetchEncryptedSecrets(String vaultUrl, String environmentNome) {
        if (vaultUrl == null || vaultUrl.isBlank()) {
            throw new SentinelEnvInjectionException("Vault URL 'SENTINEL_VAULT_URL' is missing or empty");
        }
        if (environmentNome == null || environmentNome.isBlank()) {
            throw new SentinelEnvInjectionException("Environment name parameter is missing or empty");
        }

        String targetEndpoint = vaultUrl.replaceAll("/+$", "") + "/api/v1/vault/batch-fetch";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(targetEndpoint))
                    .header("Accept", "application/json")
                    .header("X-Sentinel-Env", environmentNome)
                    .timeout(timeout)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new SentinelEnvInjectionException(
                        "Vault API returned unexpected status code [" + response.statusCode() + "] for environment: " + environmentNome
                );
            }

            Map<String, Object> responseMap = objectMapper.readValue(response.body(), new TypeReference<>() {});
            Object secretsListRaw = responseMap.get("secrets");

            if (secretsListRaw == null) {
                throw new SentinelEnvInjectionException("Vault API response payload is missing 'secrets' array");
            }

            String secretsJson = objectMapper.writeValueAsString(secretsListRaw);
            return objectMapper.readValue(secretsJson, new TypeReference<List<EncryptedSecretPayload>>() {});

        } catch (SentinelEnvInjectionException e) {
            throw e;
        } catch (Exception e) {
            throw new SentinelEnvInjectionException(
                    "Failed to communicate with Sentinel Vault Core Engine at URL: [" + targetEndpoint + "]", e
            );
        }
    }
}