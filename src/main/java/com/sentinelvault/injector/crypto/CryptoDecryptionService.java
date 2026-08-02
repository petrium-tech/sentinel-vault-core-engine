package com.sentinelvault.injector.crypto;

import com.sentinelvault.injector.core.SentinelEnvInjectionException;
import com.sentinelvault.injector.core.VolatileMemoryCleaner;
import com.sentinelvault.injector.domain.EncryptedSecretPayload;
import com.sentinelvault.injector.domain.VaultSecretEntry;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Serviço de descriptografia volátil em memória RAM usando AES-256-GCM.
 * Aplica técnicas de limpeza ativa de memória para mitigar dumps de RAM.
 */
public class CryptoDecryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // bits

    /**
     * Descriptografa um payload cifrado do Vault e limpa imediatamente os rastros de bytes da RAM.
     *
     * @param payload O payload cifrado retornado pelo Vault
     * @param base64SecretKey A chave mestre do ambiente codificada em Base64
     * @return A entrada do segredo descriptografado
     */
    public VaultSecretEntry decrypt(EncryptedSecretPayload payload, String base64SecretKey) {
        if (base64SecretKey == null || base64SecretKey.isBlank()) {
            throw new SentinelEnvInjectionException("Master crypto key 'SENTINEL_CRYPTO_KEY' is missing or empty");
        }

        byte[] keyBytes = null;
        byte[] iv = null;
        byte[] cipherTextBytes = null;
        byte[] decryptedBytes = null;

        try {
            keyBytes = Base64.getDecoder().decode(base64SecretKey);
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

            iv = Base64.getDecoder().decode(payload.initializationVector());
            cipherTextBytes = Base64.getDecoder().decode(payload.cipherText());

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            decryptedBytes = cipher.doFinal(cipherTextBytes);
            String decryptedValue = new String(decryptedBytes, StandardCharsets.UTF_8);

            return new VaultSecretEntry(payload.key(), decryptedValue);

        } catch (SentinelEnvInjectionException e) {
            throw e;
        } catch (Exception e) {
            throw new SentinelEnvInjectionException(
                    "Failed to decrypt secret payload for key: [" + payload.key() + "]", e
            );
        } finally {
            // Garantia de Zero-Disk & RAM Hygiene: Zerando os buffers brutos da memória física
            VolatileMemoryCleaner.wipe(keyBytes);
            VolatileMemoryCleaner.wipe(iv);
            VolatileMemoryCleaner.wipe(cipherTextBytes);
            VolatileMemoryCleaner.wipe(decryptedBytes);
        }
    }
}