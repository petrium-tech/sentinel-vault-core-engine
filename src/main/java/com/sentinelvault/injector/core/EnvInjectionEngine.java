package com.sentinelvault.injector.core;

import com.sentinelvault.injector.domain.InjectedPropertyBatch;
import com.sentinelvault.injector.domain.VaultSecretEntry;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.List;

/**
 * Maestro de inicialização do Sentinel Env Injector.
 * Intercepta o bootstrap do Spring Context antes da criação dos Beans para
 * injetar os segredos voláteis na memória RAM.
 */
public class EnvInjectionEngine implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final VolatileMapManager volatileMapManager;

    public EnvInjectionEngine() {
        this.volatileMapManager = new VolatileMapManager();
    }

    // Construtor utilitário para injeção de dependência em testes
    public EnvInjectionEngine(VolatileMapManager volatileMapManager) {
        this.volatileMapManager = volatileMapManager;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (applicationContext == null) {
            throw new SentinelEnvInjectionException("ApplicationContext cannot be null during bootstrap initialization");
        }

        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        // TODO: Nas próximas fases, este lote virá do VaultHttpClient e CryptoDecryptionService
        InjectedPropertyBatch initialBatch = createBootstrapMockBatch();

        // Injeta as propriedades na posição addFirst da RAM
        volatileMapManager.injectIntoEnvironment(environment, initialBatch);
    }

    /**
     * Lote temporário de verificação da Fase 1 para validar a injeção no PropertySource.
     */
    private InjectedPropertyBatch createBootstrapMockBatch() {
        VaultSecretEntry entry1 = new VaultSecretEntry("SENTINEL_INJECTOR_ACTIVE", "true");
        VaultSecretEntry entry2 = new VaultSecretEntry("SENTINEL_INJECTOR_STATUS", "RAM_INITIALIZED");

        return new InjectedPropertyBatch(VolatileMapManager.PROPERTY_SOURCE_NAME, List.of(entry1, entry2));
    }
}