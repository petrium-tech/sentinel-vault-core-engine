package com.sentinelvault.injector.core;

import com.sentinelvault.injector.crypto.CryptoDecryptionService;
import com.sentinelvault.injector.domain.EncryptedSecretPayload;
import com.sentinelvault.injector.domain.InjectedPropertyBatch;
import com.sentinelvault.injector.domain.VaultSecretEntry;
import com.sentinelvault.injector.http.VaultHttpClient;
import com.sentinelvault.injector.telemetry.SentinelTelemetryLogger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * Maestro de inicialização do Sentinel Env Injector.
 * Intercepta o bootstrap do Spring Context antes da criação dos Beans para
 * buscar, descriptografar e injetar os segredos voláteis na memória RAM.
 */
public class EnvInjectionEngine implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final String VAULT_URL_ENV = "SENTINEL_VAULT_URL";
    public static final String CRYPTO_KEY_ENV = "SENTINEL_CRYPTO_KEY";
    public static final String ENV_NAME_ENV = "SENTINEL_ENV_NAME";

    private final VolatileMapManager volatileMapManager;
    private final VaultHttpClient vaultHttpClient;
    private final CryptoDecryptionService cryptoDecryptionService;

    public EnvInjectionEngine() {
        this.volatileMapManager = new VolatileMapManager();
        this.vaultHttpClient = new VaultHttpClient();
        this.cryptoDecryptionService = new CryptoDecryptionService();
    }

    // Construtor utilitário para injeção de dependências em testes
    public EnvInjectionEngine(VolatileMapManager volatileMapManager,
                              VaultHttpClient vaultHttpClient,
                              CryptoDecryptionService cryptoDecryptionService) {
        this.volatileMapManager = volatileMapManager;
        this.vaultHttpClient = vaultHttpClient;
        this.cryptoDecryptionService = cryptoDecryptionService;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (applicationContext == null) {
            throw new SentinelEnvInjectionException("ApplicationContext cannot be null during bootstrap initialization");
        }

        long startTime = System.currentTimeMillis();
        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        // 1. Resolve as variáveis mínimas do bootstrap
        String vaultUrl = resolveProperty(environment, VAULT_URL_ENV);
        String cryptoKey = resolveProperty(environment, CRYPTO_KEY_ENV);
        String envName = resolveProperty(environment, ENV_NAME_ENV, "DEVELOPMENT");

        if (vaultUrl == null || vaultUrl.isBlank() || cryptoKey == null || cryptoKey.isBlank()) {
            SentinelTelemetryLogger.logSkipped("SENTINEL_VAULT_URL or SENTINEL_CRYPTO_KEY missing in environment.");
            return;
        }

        SentinelTelemetryLogger.logStart(vaultUrl, envName);

        // 2. Busca os payloads cifrados do Vault Core Engine
        List<EncryptedSecretPayload> encryptedPayloads = vaultHttpClient.fetchEncryptedSecrets(vaultUrl, envName);

        // 3. Descriptografa cada segredo volátil em memória
        List<VaultSecretEntry> decryptedEntries = new ArrayList<>();
        for (EncryptedSecretPayload payload : encryptedPayloads) {
            VaultSecretEntry decryptedEntry = cryptoDecryptionService.decrypt(payload, cryptoKey);
            decryptedEntries.add(decryptedEntry);
        }

        // 4. Injeta as propriedades na posição de prioridade máxima da RAM (addFirst)
        InjectedPropertyBatch batch = new InjectedPropertyBatch(VolatileMapManager.PROPERTY_SOURCE_NAME, decryptedEntries);
        volatileMapManager.injectIntoEnvironment(environment, batch);

        long executionTimeMs = System.currentTimeMillis() - startTime;
        SentinelTelemetryLogger.logSuccess(decryptedEntries.size(), executionTimeMs);
    }

    private String resolveProperty(ConfigurableEnvironment environment, String key) {
        return resolveProperty(environment, key, null);
    }

    private String resolveProperty(ConfigurableEnvironment environment, String key, String defaultValue) {
        String value = environment.getProperty(key);
        if (value != null && !value.isBlank()) {
            return value;
        }
        value = System.getenv(key);
        if (value != null && !value.isBlank()) {
            return value;
        }
        return defaultValue;
    }
}