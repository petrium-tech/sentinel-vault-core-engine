package com.sentinelvault.injector.http;

import com.sentinelvault.injector.core.SentinelEnvInjectionException;
import com.sentinelvault.injector.domain.EncryptedSecretPayload;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VaultHttpClientTest {

    private HttpServer server;
    private VaultHttpClient vaultHttpClient;
    private String serverUrl;

    @BeforeEach
    void setUp() throws Exception {
        // Servidor HTTP em memória para simular o Vault Core Engine
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.start();
        int port = server.getAddress().getPort();
        serverUrl = "http://localhost:" + port;
        vaultHttpClient = new VaultHttpClient(Duration.ofSeconds(2));
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    @DisplayName("Deve buscar e deserializar com sucesso os segredos cifrados do Vault")
    void shouldFetchAndDeserializeSecretsSuccessfully() {
        // Given: Um mock de resposta do Vault Core Engine
        String mockJsonResponse = """
            {
              "environment": "DEVELOPMENT",
              "totalRecords": 1,
              "secrets": [
                {
                  "key": "SPRING_DATASOURCE_PASSWORD",
                  "cipherText": "encryptedBytesHex",
                  "initializationVector": "ivHex"
                }
              ]
            }
            """;

        server.createContext("/api/v1/vault/batch-fetch", exchange -> {
            exchange.sendResponseHeaders(200, mockJsonResponse.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(mockJsonResponse.getBytes());
            }
        });

        // When
        List<EncryptedSecretPayload> result = vaultHttpClient.fetchEncryptedSecrets(serverUrl, "DEVELOPMENT");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).key()).isEqualTo("SPRING_DATASOURCE_PASSWORD");
        assertThat(result.get(0).cipherText()).isEqualTo("encryptedBytesHex");
    }

    @Test
    @DisplayName("Deve lançar SentinelEnvInjectionException quando o Vault retornar status 500 ou 404")
    void shouldThrowExceptionWhenVaultReturnsErrorStatusCode() {
        server.createContext("/api/v1/vault/batch-fetch", exchange -> {
            exchange.sendResponseHeaders(500, 0);
            exchange.close();
        });

        assertThatThrownBy(() -> vaultHttpClient.fetchEncryptedSecrets(serverUrl, "DEVELOPMENT"))
                .isInstanceOf(SentinelEnvInjectionException.class)
                .hasMessageContaining("Vault API returned unexpected status code [500]");
    }

    @Test
    @DisplayName("Deve lançar SentinelEnvInjectionException quando a URL do Vault for nula ou vazia")
    void shouldThrowExceptionWhenUrlIsEmpty() {
        assertThatThrownBy(() -> vaultHttpClient.fetchEncryptedSecrets("", "DEVELOPMENT"))
                .isInstanceOf(SentinelEnvInjectionException.class)
                .hasMessageContaining("Vault URL 'SENTINEL_VAULT_URL' is missing or empty");
    }
}