package com.sentinelvault.domain.service;

import com.sentinelvault.domain.exception.BusinessException;
import com.sentinelvault.repository.CredentialAuditLogRepository;
import com.sentinelvault.repository.CredentialRepository;
import com.sentinelvault.repository.UserRepository;
import com.sentinelvault.repository.entity.CredentialAuditLogEntity;
import com.sentinelvault.repository.entity.CredentialEntity;
import com.sentinelvault.repository.entity.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID; // IMPORT CORRETO ADICIONADO AQUI

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
    public List<CredentialEntity> findCredentialsByUserEmail(String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("User not found."));

        List<CredentialEntity> credentials = credentialRepository.findByUserId(user.getId());

        credentials.forEach(entity -> {
            String decryptedPassword = cryptoService.decrypt(entity.getPasswordPayload());
            entity.setPasswordPayload(decryptedPassword);

            CredentialAuditLogEntity auditLog = new CredentialAuditLogEntity(
                    entity.getId(),
                    user.getId(),
                    "READ_DECRYPT"
            );
            auditLogRepository.save(auditLog);
        });

        return credentials;
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