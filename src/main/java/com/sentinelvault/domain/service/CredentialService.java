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
    public CredentialEntity registerCredential(String name, String serviceUrl, String username, String plainPassword,
                                               String userEmail, String ipAddress, String userAgent) {
        UserEntity user = getUserOrThrow(userEmail, "User owner not found.");

        String encryptedPassword = cryptoService.encrypt(plainPassword);

        CredentialEntity entity = new CredentialEntity();
        entity.setName(name);
        entity.setServiceUrl(serviceUrl);
        entity.setUsernamePayload(username);
        entity.setPasswordPayload(encryptedPassword);
        entity.setUser(user);

        CredentialEntity savedCredential = credentialRepository.save(entity);

        // DevSecOps: Telemetria de auditoria centralizada
        logAudit(savedCredential.getId(), user.getId(), "CREATE", ipAddress, userAgent);

        return savedCredential;
    }

    @Transactional
    public Page<CredentialEntity> findCredentialsByUserEmail(String userEmail, Pageable pageable,
                                                             String ipAddress, String userAgent) {
        UserEntity user = getUserOrThrow(userEmail, "User not found.");

        Page<CredentialEntity> credentialsPage = credentialRepository.findByUserId(user.getId(), pageable);

        credentialsPage.forEach(entity -> {
            String decryptedPassword = cryptoService.decrypt(entity.getPasswordPayload());
            entity.setPasswordPayload(decryptedPassword);

            // DevSecOps: Rastro persistente de descriptografia em lote
            logAudit(entity.getId(), user.getId(), "READ_DECRYPT", ipAddress, userAgent);
        });

        return credentialsPage;
    }

    @Transactional
    public void deleteCredential(UUID credentialId, String userEmail, String ipAddress, String userAgent) {
        UserEntity user = getUserOrThrow(userEmail, "User not found.");

        CredentialEntity credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new BusinessException("Credential not found or already deleted."));

        // Proteção contra Broken Object Level Authorization (BOLA)
        if (!credential.getUser().getId().equals(user.getId())) {
            throw new BusinessException("You do not have permission to delete this credential.");
        }

        credentialRepository.delete(credential);

        // DevSecOps: Rastro imutável de deleção lógica (Soft Delete)
        logAudit(credentialId, user.getId(), "DELETE_SOFT", ipAddress, userAgent);
    }

    private UserEntity getUserOrThrow(String userEmail, String exceptionMessage) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(exceptionMessage));
    }

    private void logAudit(UUID credentialId, UUID userId, String action, String ipAddress, String userAgent) {
        CredentialAuditLogEntity auditLog = new CredentialAuditLogEntity(
                credentialId,
                userId,
                action,
                ipAddress,
                userAgent
        );
        auditLogRepository.save(auditLog);
    }
}