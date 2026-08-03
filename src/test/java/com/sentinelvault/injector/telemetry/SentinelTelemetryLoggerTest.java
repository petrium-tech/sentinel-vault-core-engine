package com.sentinelvault.injector.telemetry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class SentinelTelemetryLoggerTest {

    @Test
    @DisplayName("Deve emitir os logs de telemetria sem lançar exceções")
    void shouldLogTelemetryWithoutExceptions() {
        assertThatCode(() -> {
            SentinelTelemetryLogger.logStart("http://localhost:9090", "DEVELOPMENT");
            SentinelTelemetryLogger.logSuccess(5, 120);
            SentinelTelemetryLogger.logSkipped("Missing environment properties");
        }).doesNotThrowAnyException();
    }
}