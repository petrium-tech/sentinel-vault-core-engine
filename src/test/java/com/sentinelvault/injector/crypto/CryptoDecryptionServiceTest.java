package com.sentinelvault.injector.crypto;

import com.sentinelvault.injector.core.SentinelEnvInjectionException;
import com.sentinelvault.injector.domain.EncryptedSecretPayload;
import com.sentinelvault.injector.domain.VaultSecretEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CryptoDecryptionServiceTest {

    private CryptoDecryptionService cryptoService;
    private String masterKeyBase64;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() throws Exception {
        cryptoService = new CryptoDecryptionService();

        // Gerador de chave AES-256 em memória para os testes
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        this.secretKey = keyGen.generateKey();
        this.masterKeyBase64 = Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    @Test
    @DisplayName("Deve descriptografar o payload cifrado em AES-256-GCM com sucesso")
    void shouldDecryptPayloadSuccessfully() throws Exception {
        // Given: Um segredo cifrado via AES-GCM
        String rawSecretKey = "SPRING_DATASOURCE_PASSWORD";
        String rawSecretValue = "superSecretPassword123!";

        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
        byte[] encryptedBytes = cipher.doFinal(rawSecretValue.getBytes(StandardCharsets.UTF_8));

        EncryptedSecretPayload payload = new EncryptedSecretPayload(
                rawSecretKey,
                Base64.getEncoder().encodeToString(encryptedBytes),
                Base64.getEncoder().encodeToString(iv)
        );

        // When: Dispara a descriptografia
        VaultSecretEntry result = cryptoService.decrypt(payload, masterKeyBase64);

        // Then
        assertThat(result.key()).isEqualTo(rawSecretKey);
        assertThat(result.value()).isEqualTo(rawSecretValue);
    }

    @Test
    @DisplayName("Deve lançar SentinelEnvInjectionException caso a chave mestre seja nula")
    void shouldThrowExceptionWhenMasterKeyIsNull() {
        EncryptedSecretPayload payload = new EncryptedSecretPayload("KEY", "cipherText", "iv");

        assertThatThrownBy(() -> cryptoService.decrypt(payload, null))
                .isInstanceOf(SentinelEnvInjectionException.class)
                .hasMessageContaining("Master crypto key 'SENTINEL_CRYPTO_KEY' is missing or empty");
    }

    @Test
    @DisplayName("Deve lançar SentinelEnvInjectionException se o ciphertext for inválido/corrompido")
    void shouldThrowExceptionWhenPayloadIsCorrupted() {
        EncryptedSecretPayload corruptedPayload = new EncryptedSecretPayload(
                "INVALID_KEY",
                Base64.getEncoder().encodeToString("corruptedData".getBytes()),
                Base64.getEncoder().encodeToString(new byte[12])
        );

        assertThatThrownBy(() -> cryptoService.decrypt(corruptedPayload, masterKeyBase64))
                .isInstanceOf(SentinelEnvInjectionException.class)
                .hasMessageContaining("Failed to decrypt secret payload");
    }
}