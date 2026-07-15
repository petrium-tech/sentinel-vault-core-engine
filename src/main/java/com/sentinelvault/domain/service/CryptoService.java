package com.sentinelvault.domain.service;

import com.sentinelvault.domain.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH_BYTES = 12; // IV recomendado para GCM (96 bits)
    private static final int GCM_TAG_LENGTH_BITS = 128; // Tamanho da tag de autenticação

    private final byte[] secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    // Injeta a chave secreta definida no application.yaml
    public CryptoService(@Value("${sentinel.security.crypto-key}") String base64Key) {
        try {
            this.secretKey = Base64.getDecoder().decode(base64Key);
            if (this.secretKey.length != 32) { // 32 bytes * 8 = 256 bits
                throw new IllegalArgumentException("The secret key must be exactly 256 bits (32 bytes) after Base64 decoding.");
            }
        } catch (Exception e) {
            throw new BusinessException("Failed to initialize cryptographic key. Ensure 'sentinel.security.crypto-key' is a valid 32-byte Base64 string.");
        }
    }

    /**
     * Criptografa um texto puro usando AES-256-GCM.
     * O IV é gerado de forma aleatória e concatenado junto ao texto cifrado.
     */
    public String encrypt(String plainText) {
        if (plainText == null) return null;
        try {
            // 1. Gerar um IV seguro e aleatório de 12 bytes para esta operação
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            secureRandom.nextBytes(iv);

            // 2. Configurar o Cipher para encriptação
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);

            // 3. Executar a criptografia do payload
            byte[] cipherText = cipher.doFinal(plainText.getBytes());

            // 4. Concatenar o IV e o Texto Cifrado em um único array de bytes para armazenamento fácil
            byte[] ivAndCipherText = ByteBuffer.allocate(iv.length + cipherText.length)
                    .put(iv)
                    .put(cipherText)
                    .array();

            // 5. Retornar como String Base64 segura para o banco
            return Base64.getEncoder().encodeToString(ivAndCipherText);

        } catch (Exception e) {
            throw new BusinessException("Encryption error: " + e.getMessage());
        }
    }

    /**
     * Descriptografa um texto Base64 usando AES-256-GCM.
     * Extrai o IV embutido nos primeiros 12 bytes do array para descriptografar o restante do payload.
     */
    public String decrypt(String encryptedTextBase64) {
        if (encryptedTextBase64 == null) return null;
        try {
            // 1. Decodificar a String Base64 de volta para o array de bytes
            byte[] ivAndCipherText = Base64.getDecoder().decode(encryptedTextBase64);

            // 2. Extrair o IV de 12 bytes
            ByteBuffer byteBuffer = ByteBuffer.wrap(ivAndCipherText);
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            byteBuffer.get(iv);

            // 3. Extrair o restante do array que é o Texto Cifrado real
            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            // 4. Configurar o Cipher para descriptografia usando o IV extraído
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey, "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);

            // 5. Decifrar os bytes e retornar como string
            byte[] decryptedTextBytes = cipher.doFinal(cipherText);
            return new String(decryptedTextBytes);

        } catch (Exception e) {
            throw new BusinessException("Decryption error: " + e.getMessage());
        }
    }
}