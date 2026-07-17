package com.sentinelvault.repository;

import com.sentinelvault.repository.entity.CredentialAuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CredentialAuditLogRepository extends JpaRepository<CredentialAuditLogEntity, UUID> {
}