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
    public CredentialEntity registerCredential(String name, String serviceUrl, String username, String plainPassword, String userEmail, String ipAddress, String userAgent) {
        // Busca o usuário dono de forma blindada utilizando o e-mail extraído do JWT [cite: 2079]
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("User owner not found."));

        // Executa a criptografia simétrica AES-256-GCM na senha do segredo [cite: 2215]
        String encryptedPassword = cryptoService.encrypt(plainPassword);

        // Monta e persiste a entidade no PostgreSQL
        CredentialEntity entity = new CredentialEntity();
        entity.setName(name);
        entity.setServiceUrl(serviceUrl);
        entity.setUsernamePayload(username);
        entity.setPasswordPayload(encryptedPassword);
        entity.setUser(user);

        CredentialEntity savedCredential = credentialRepository.save(entity);

        // DevSecOps: Registra a auditoria contendo a telemetria do contexto de rede
        CredentialAuditLogEntity auditLog = new CredentialAuditLogEntity(
                savedCredential.getId(),
                user.getId(),
                "CREATE",
                ipAddress,
                userAgent
        );
        auditLogRepository.save(auditLog);

        return savedCredential;
    }

    @Transactional
    public Page<CredentialEntity> findCredentialsByUserEmail(String userEmail, Pageable pageable, String ipAddress, String userAgent) {
        // Garante que o usuário existe buscando pelo e-mail do JWT [cite: 2079]
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("User not found."));

        // Busca o bloco paginado de credenciais cifradas pertencentes a este ID de usuário [cite: 2225]
        Page<CredentialEntity> credentialsPage = credentialRepository.findByUserId(user.getId(), pageable);

        // Descriptografa o payload da senha em tempo de execução e dispara a auditoria para cada uma encontrada na página [cite: 2225]
        credentialsPage.forEach(entity -> {
            String decryptedPassword = cryptoService.decrypt(entity.getPasswordPayload());
            entity.setPasswordPayload(decryptedPassword);

            // DevSecOps: Auditoria persistente enriquecida com IP e User-Agent do solicitante
            CredentialAuditLogEntity auditLog = new CredentialAuditLogEntity(
                    entity.getId(),
                    user.getId(),
                    "READ_DECRYPT",
                    ipAddress,
                    userAgent
            );
            auditLogRepository.save(auditLog);
        });

        return credentialsPage;
    }

    @Transactional
    public void deleteCredential(UUID credentialId, String userEmail, String ipAddress, String userAgent) {
        // Garante que o usuário logado existe
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("User not found."));

        // Busca a credencial ativa. Se já estiver deletada logicamente, o Hibernate não vai achar por causa do @SQLRestriction
        CredentialEntity credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new BusinessException("Credential not found or already deleted."));

        // Validação de Segurança: Garante que o usuário só pode deletar o que é dele
        if (!credential.getUser().getId().equals(user.getId())) {
            throw new BusinessException("You do not have permission to delete this credential.");
        }

        credentialRepository.delete(credential);

        // DevSecOps: Preserva o rastro detalhado de rede de quem comandou a remoção do segredo
        CredentialAuditLogEntity auditLog = new CredentialAuditLogEntity(
                credentialId,
                user.getId(),
                "DELETE_SOFT",
                ipAddress,
                userAgent
        );
        auditLogRepository.save(auditLog);
    }
}