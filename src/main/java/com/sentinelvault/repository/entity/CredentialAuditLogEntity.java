package com.sentinelvault.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "credential_audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialAuditLogEntity {

    @Id
    private UUID id;

    @Column(name = "credential_id", nullable = false)
    private UUID credentialId;

    @Column(name = "accessed_by_user_id", nullable = false)
    private UUID accessedByUserId;

    @Column(name = "action_performed", nullable = false, length = 50)
    private String actionPerformed;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "accessed_at", nullable = false)
    private OffsetDateTime accessedAt;

    /**
     * CONSTRUTOR DE COMPATIBILIDADE (3 Parâmetros)
     * Resolve o erro de compilação atual permitindo chamadas legadas de criação de log.
     */
    public CredentialAuditLogEntity(UUID credentialId, UUID accessedByUserId, String actionPerformed) {
        this.id = UUID.randomUUID();
        this.credentialId = credentialId;
        this.accessedByUserId = accessedByUserId;
        this.actionPerformed = actionPerformed;
        this.ipAddress = "UNKNOWN";
        this.userAgent = "UNKNOWN";
        this.accessedAt = OffsetDateTime.now();
    }

    /**
     * CONSTRUTOR DE TELEMETRIA AVANÇADA (5 Parâmetros)
     * Utilizado para registrar o contexto completo de rede (IP e User-Agent).
     */
    public CredentialAuditLogEntity(UUID credentialId, UUID accessedByUserId, String actionPerformed, String ipAddress, String userAgent) {
        this.id = UUID.randomUUID();
        this.credentialId = credentialId;
        this.accessedByUserId = accessedByUserId;
        this.actionPerformed = actionPerformed;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.accessedAt = OffsetDateTime.now();
    }
}