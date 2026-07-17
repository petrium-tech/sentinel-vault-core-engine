package com.sentinelvault.domain.service;

import com.sentinelvault.domain.exception.BusinessException;
import com.sentinelvault.repository.CredentialAuditLogRepository;
import com.sentinelvault.repository.CredentialRepository;
import com.sentinelvault.repository.UserRepository;
import com.sentinelvault.repository.entity.CredentialAuditLogEntity;
import com.sentinelvault.repository.entity.CredentialEntity;
import com.sentinelvault.repository.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final CryptoService cryptoService;
    private final CredentialAuditLogRepository auditLogRepository;

    public CredentialService(CredentialRepository credentialRepository,
                             UserRepository userRepository,
                             CryptoService cryptoService,
                             CredentialAuditLogRepository auditLogRepository) {
        this.credentialRepository = credentialRepository;
        this.userRepository = userRepository;
        this.cryptoService = cryptoService;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public CredentialEntity registerCredential(String name, String serviceUrl, String username, String plainPassword, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("User owner not found."));

        String encryptedPassword = cryptoService.encrypt(plainPassword);

        CredentialEntity entity = new CredentialEntity();
        entity.setName(name);
        entity.setServiceUrl(serviceUrl);
        entity.setUsernamePayload(username);
        entity.setPasswordPayload(encryptedPassword);
        entity.setUser(user);

        CredentialEntity savedCredential = credentialRepository.save(entity);

        CredentialAuditLogEntity auditLog = new CredentialAuditLogEntity(
                savedCredential.getId(),
                user.getId(),
                "CREATE"
        );
        auditLogRepository.save(auditLog);

        return savedCredential;
    }

    @Transactional
    public Page<CredentialEntity> findCredentialsByUserEmail(String userEmail, Pageable pageable) {
        // 1. Garante que o usuário existe buscando pelo e-mail do JWT
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("User not found."));

        // 2. Busca o bloco paginado de credenciais cifradas pertencentes a este ID de usuário
        Page<CredentialEntity> credentialsPage = credentialRepository.findByUserId(user.getId(), pageable);

        // 3. Descriptografa o payload da senha em tempo de execução e dispara a auditoria para cada uma encontrada na página
        credentialsPage.forEach(entity -> {
            String decryptedPassword = cryptoService.decrypt(entity.getPasswordPayload());
            entity.setPasswordPayload(decryptedPassword);

            // 4. Auditoria persistente
            CredentialAuditLogEntity auditLog = new CredentialAuditLogEntity(
                    entity.getId(),
                    user.getId(),
                    "READ_DECRYPT"
            );
            auditLogRepository.save(auditLog);
        });

        return credentialsPage;
    }

    @Transactional
    public void deleteCredential(UUID credentialId, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("User not found."));

        CredentialEntity credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new BusinessException("Credential not found or already deleted."));

        if (!credential.getUser().getId().equals(user.getId())) {
            throw new BusinessException("You do not have permission to delete this credential.");
        }

        credentialRepository.delete(credential);

        CredentialAuditLogEntity auditLog = new CredentialAuditLogEntity(
                credentialId,
                user.getId(),
                "DELETE_SOFT"
        );
        auditLogRepository.save(auditLog);
    }
}