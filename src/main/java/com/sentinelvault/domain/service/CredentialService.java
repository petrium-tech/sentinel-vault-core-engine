package com.sentinelvault.domain.service;

import com.sentinelvault.domain.exception.BusinessException;
import com.sentinelvault.repository.CredentialRepository;
import com.sentinelvault.repository.UserRepository;
import com.sentinelvault.repository.entity.CredentialEntity;
import com.sentinelvault.repository.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public CredentialEntity registerCredential(String name, String serviceUrl, String username, String plainPassword, String userEmail) {
        // 1. Busca o usuário dono de forma blindada utilizando o e-mail extraído do JWT
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("User owner not found."));

        // 2. Executa a criptografia simétrica AES-256-GCM na senha do segredo
        String encryptedPassword = cryptoService.encrypt(plainPassword);

        // 3. Monta e persiste a entidade no PostgreSQL
        CredentialEntity entity = new CredentialEntity();
        entity.setName(name);
        entity.setServiceUrl(serviceUrl);
        entity.setUsernamePayload(username);
        entity.setPasswordPayload(encryptedPassword); // Grava o texto cifrado (Ciphertext)
        entity.setUser(user);

        return credentialRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<CredentialEntity> findCredentialsByUserEmail(String userEmail) {
        // 1. Garante que o usuário existe buscando pelo e-mail do JWT
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("User not found."));

        // 2. Busca todas as credenciais cifradas pertencentes a este ID de usuário
        List<CredentialEntity> credentials = credentialRepository.findByUserId(user.getId());

        // 3. Descriptografa o payload da senha em tempo de execução para cada registro
        credentials.forEach(entity -> {
            String decryptedPassword = cryptoService.decrypt(entity.getPasswordPayload());
            entity.setPasswordPayload(decryptedPassword); // Substituição segura em memória
        });

        return credentials;
    }
}