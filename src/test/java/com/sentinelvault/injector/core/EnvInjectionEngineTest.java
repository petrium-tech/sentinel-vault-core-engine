package com.sentinelvault.injector.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnvInjectionEngineTest {

    private EnvInjectionEngine engine;

    @BeforeEach
    void setUp() {
        engine = new EnvInjectionEngine();
    }

    @Test
    @DisplayName("Deve injetar as propriedades voláteis na memória RAM durante a inicialização do contexto")
    void shouldInjectVolatilePropertiesIntoEnvironmentRam() {
        // Given: Um contexto de aplicação Spring limpo
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // When: O motor de injeção é executado no bootstrap
        engine.initialize(context);

        // Then: O ambiente do Spring deve conter as propriedades injetadas
        ConfigurableEnvironment environment = context.getEnvironment();

        assertThat(environment.getProperty("SENTINEL_INJECTOR_ACTIVE"))
                .isEqualTo("true");

        assertThat(environment.getProperty("SENTINEL_INJECTOR_STATUS"))
                .isEqualTo("RAM_INITIALIZED");
    }

    @Test
    @DisplayName("Deve garantir que o PropertySource do injetor seja inserido com prioridade máxima (addFirst)")
    void shouldEnsurePropertySourceHasHighestPriority() {
        // Given
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // When
        engine.initialize(context);

        // Then: O primeiro PropertySource na fila do Spring deve ser o do Sentinel Injector
        PropertySource<?> firstSource = context.getEnvironment()
                .getPropertySources()
                .iterator()
                .next();

        assertThat(firstSource.getName())
                .isEqualTo(VolatileMapManager.PROPERTY_SOURCE_NAME);
    }

    @Test
    @DisplayName("Deve lançar SentinelEnvInjectionException se o contexto do Spring for nulo (Fail-Fast)")
    void shouldThrowExceptionWhenContextIsNull() {
        assertThatThrownBy(() -> engine.initialize(null))
                .isInstanceOf(SentinelEnvInjectionException.class)
                .hasMessageContaining("ApplicationContext cannot be null");
    }
}