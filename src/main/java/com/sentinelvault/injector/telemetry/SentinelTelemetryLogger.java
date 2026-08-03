package com.sentinelvault.injector.telemetry;

import java.util.logging.Logger;

/**
 * Utilitário de telemetria e logs auditáveis sanitizados para o Sentinel Env Injector.
 * Garante visibilidade da execução sem jamais expor dados sensíveis ou chaves.
 */
public final class SentinelTelemetryLogger {

    private static final Logger LOGGER = Logger.getLogger(SentinelTelemetryLogger.class.getName());
    private static final String PREFIX = "[SENTINEL-INJECTOR] ";

    private SentinelTelemetryLogger() {
        // Construtor privado para classe utilitária
    }

    public static void logStart(String vaultUrl, String environment) {
        LOGGER.info(PREFIX + "Starting volatile environment bootstrap interception...");
        LOGGER.info(PREFIX + "Connecting to Vault Core Engine at [" + vaultUrl + "] for Environment: [" + environment + "]");
    }

    public static void logSuccess(int secretCount, long executionTimeMs) {
        LOGGER.info(PREFIX + secretCount + " secret(s) fetched, decrypted in-memory (AES-256-GCM), and injected into RAM.");
        LOGGER.info(PREFIX + "PropertySource priority set to MAXIMUM (addFirst).");
        LOGGER.info(PREFIX + "RAM memory hygiene executed (byte buffers wiped with zero-fill).");
        LOGGER.info(PREFIX + "Bootstrap injection completed successfully in " + executionTimeMs + " ms.");
    }

    public static void logSkipped(String reason) {
        LOGGER.warning(PREFIX + "Injection skipped: " + reason);
    }
}