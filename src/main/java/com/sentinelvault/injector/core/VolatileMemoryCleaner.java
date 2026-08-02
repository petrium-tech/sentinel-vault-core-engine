package com.sentinelvault.injector.core;

import java.util.Arrays;

/**
 * Utilitário responsável pela higienização ativa de memória RAM, sobrescrevendo
 * buffers de dados sensíveis com zeros após o término do bootstrap.
 */
public final class VolatileMemoryCleaner {

    private VolatileMemoryCleaner() {
        // Construtor privado para evitar instanciação de classe utilitária
    }

    /**
     * Sobrescreve um array de bytes com zeros na memória RAM.
     *
     * @param buffer O array de bytes contendo dados sensíveis
     */
    public static void wipe(byte[] buffer) {
        if (buffer != null) {
            Arrays.fill(buffer, (byte) 0);
        }
    }

    /**
     * Sobrescreve um array de caracteres com zeros na memória RAM.
     *
     * @param buffer O array de chars contendo dados sensíveis
     */
    public static void wipe(char[] buffer) {
        if (buffer != null) {
            Arrays.fill(buffer, '\0');
        }
    }
}