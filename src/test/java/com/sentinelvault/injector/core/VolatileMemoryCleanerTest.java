package com.sentinelvault.injector.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VolatileMemoryCleanerTest {

    @Test
    @DisplayName("Deve preencher todo o array de bytes com zeros após a chamada de higienização")
    void shouldWipeByteArrayWithZeros() {
        // Given: Um buffer contendo bytes sensíveis
        byte[] sensitiveBytes = "SuperSecretMasterKey123".getBytes();

        // When: Aplica a higienização de memória
        VolatileMemoryCleaner.wipe(sensitiveBytes);

        // Then: Todos os elementos do array devem ser 0
        for (byte b : sensitiveBytes) {
            assertThat(b).isEqualTo((byte) 0);
        }
    }

    @Test
    @DisplayName("Deve preencher todo o array de chars com null-character após a chamada de higienização")
    void shouldWipeCharArrayWithNullChars() {
        // Given
        char[] sensitiveChars = "MySecretPassword".toCharArray();

        // When
        VolatileMemoryCleaner.wipe(sensitiveChars);

        // Then
        for (char c : sensitiveChars) {
            assertThat(c).isEqualTo('\0');
        }
    }

    @Test
    @DisplayName("Não deve lançar NullPointerException ao passar arrays nulos")
    void shouldHandleNullArraysGracefully() {
        byte[] nullBytes = null;
        char[] nullChars = null;

        VolatileMemoryCleaner.wipe(nullBytes);
        VolatileMemoryCleaner.wipe(nullChars);

        assertThat(nullBytes).isNull();
        assertThat(nullChars).isNull();
    }
}