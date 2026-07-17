package com.sentinelvault.repository;

import com.sentinelvault.repository.entity.CredentialEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CredentialRepository extends JpaRepository<CredentialEntity, UUID> {
    Page<CredentialEntity> findByUserId(UUID userId, Pageable pageable);
}