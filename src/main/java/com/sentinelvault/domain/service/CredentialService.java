package com.sentinelvault.domain.service;

import com.sentinelvault.domain.exception.BusinessException;
import com.sentinelvault.repository.CredentialRepository;
import com.sentinelvault.repository.UserRepository;
import com.sentinelvault.repository.entity.CredentialEntity;
import com.sentinelvault.repository.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final CryptoService cryptoService;

    public CredentialService(CredentialRepository credentialRepository, UserRepository userRepository, CryptoService cryptoService) {
        this.credentialRepository = credentialRepository;
        this.userRepository = userRepository;
        this.cryptoService = cryptoService;
    }

    @Transactional
    public CredentialEntity registerCredential(String name, String serviceUrl, String username, String plainPassword, UUID userId) {
        // 1. Busca o usuário dono da credencial no banco
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User owner not found."));

        // 2. Executa a criptografia simétrica AES-256-GCM na senha do segredo
        String encryptedPassword = cryptoService.encrypt(plainPassword);

        // 3. Monta e persiste a entidade no PostgreSQL
        CredentialEntity entity = new CredentialEntity();
        entity.setName(name);
        entity.setServiceUrl(serviceUrl);
        entity.setUsernamePayload(username);
        entity.setPasswordPayload(encryptedPassword); // Grava a senha criptografada (Ciphertext)
        entity.setUser(user);

        return credentialRepository.save(entity);
    }
}