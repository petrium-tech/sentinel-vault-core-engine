package com.sentinelvault.injector.core;

import com.sentinelvault.injector.crypto.CryptoDecryptionService;
import com.sentinelvault.injector.domain.EncryptedSecretPayload;
import com.sentinelvault.injector.domain.VaultSecretEntry;
import com.sentinelvault.injector.http.VaultHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnvInjectionEngineTest {

    @Mock
    private VolatileMapManager volatileMapManager;

    @Mock
    private VaultHttpClient vaultHttpClient;

    @Mock
    private CryptoDecryptionService cryptoDecryptionService;

    private EnvInjectionEngine engine;

    @BeforeEach
    void setUp() {
        engine = new EnvInjectionEngine(new VolatileMapManager(), vaultHttpClient, cryptoDecryptionService);
    }

    @Test
    @DisplayName("Deve buscar, descriptografar e injetar segredos na RAM com sucesso durante a inicialização")
    void shouldFetchDecryptAndInjectSecretsIntoRamSuccessfully() {
        // Given
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        ConfigurableEnvironment env = context.getEnvironment();
        env.getPropertySources().addFirst(new MapPropertySource("testEnv", Map.of(
                "SENTINEL_VAULT_URL", "http://localhost:9090",
                "SENTINEL_CRYPTO_KEY", "YTM0N2M5ZjgxYjIyYzM0ZDVkNmU3YThmOTBlYTIxY2Q="
        )));

        EncryptedSecretPayload mockPayload = new EncryptedSecretPayload("SPRING_DATASOURCE_PASSWORD", "cipherTextHex", "ivHex");
        VaultSecretEntry mockDecryptedEntry = new VaultSecretEntry("SPRING_DATASOURCE_PASSWORD", "secret123!");

        when(vaultHttpClient.fetchEncryptedSecrets("http://localhost:9090", "DEVELOPMENT"))
                .thenReturn(List.of(mockPayload));
        when(cryptoDecryptionService.decrypt(any(), eq("YTM0N2M5ZjgxYjIyYzM0ZDVkNmU3YThmOTBlYTIxY2Q=")))
                .thenReturn(mockDecryptedEntry);

        // When
        engine.initialize(context);

        // Then
        assertThat(context.getEnvironment().getProperty("SPRING_DATASOURCE_PASSWORD"))
                .isEqualTo("secret123!");
    }

    @Test
    @DisplayName("Deve lançar SentinelEnvInjectionException se SENTINEL_VAULT_URL não for informada")
    void shouldThrowExceptionWhenVaultUrlIsMissing() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        assertThatThrownBy(() -> engine.initialize(context))
                .isInstanceOf(SentinelEnvInjectionException.class)
                .hasMessageContaining("Missing required environment property: [SENTINEL_VAULT_URL]");
    }

    @Test
    @DisplayName("Deve lançar SentinelEnvInjectionException se o contexto do Spring for nulo")
    void shouldThrowExceptionWhenContextIsNull() {
        assertThatThrownBy(() -> engine.initialize(null))
                .isInstanceOf(SentinelEnvInjectionException.class)
                .hasMessageContaining("ApplicationContext cannot be null");
    }
}