package com.sentinelvault.repository;

import com.sentinelvault.repository.entity.CredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CredentialRepository extends JpaRepository<CredentialEntity, UUID> {

    List<CredentialEntity> findByUserId(UUID userId);
}