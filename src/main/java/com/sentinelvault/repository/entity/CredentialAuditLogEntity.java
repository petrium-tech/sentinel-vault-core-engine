package com.sentinelvault.repository.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "credential_audit_logs")
public class CredentialAuditLogEntity {

    @Id
    private UUID id;

    @Column(name = "credential_id", nullable = false)
    private UUID credentialId;

    @Column(name = "accessed_by_user_id", nullable = false)
    private UUID accessedByUserId;

    @Column(name = "action_performed", nullable = false, length = 50)
    private String actionPerformed;

    @Column(name = "accessed_at", nullable = false)
    private OffsetDateTime accessedAt;

    public CredentialAuditLogEntity() {
    }

    public CredentialAuditLogEntity(UUID credentialId, UUID accessedByUserId, String actionPerformed) {
        this.id = UUID.randomUUID();
        this.credentialId = credentialId;
        this.accessedByUserId = accessedByUserId;
        this.actionPerformed = actionPerformed;
        this.accessedAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCredentialId() { return credentialId; }
    public void setCredentialId(UUID credentialId) { this.credentialId = credentialId; }

    public UUID getAccessedByUserId() { return accessedByUserId; }
    public void setAccessedByUserId(UUID accessedByUserId) { this.accessedByUserId = accessedByUserId; }

    public String getActionPerformed() { return actionPerformed; }
    public void setActionPerformed(String actionPerformed) { this.actionPerformed = actionPerformed; }

    public OffsetDateTime getAccessedAt() { return accessedAt; }
    public void setAccessedAt(OffsetDateTime accessedAt) { this.accessedAt = accessedAt; }
}