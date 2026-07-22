package com.sentinelvault.injector.core;

/**
 * Exceção lançada quando ocorre qualquer falha fatal durante a fase de injeção
 * de propriedades em memória RAM, interrompendo o boot da JVM.
 */
public class SentinelEnvInjectionException extends RuntimeException {

    public SentinelEnvInjectionException(String message) {
        super(message);
    }

    public SentinelEnvInjectionException(String message, Throwable cause) {
        super(message, cause);
    }
}