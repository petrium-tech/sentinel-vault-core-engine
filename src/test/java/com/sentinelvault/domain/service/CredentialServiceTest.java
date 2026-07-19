package com.sentinelvault.domain.service;

import com.sentinelvault.domain.exception.BusinessException;
import com.sentinelvault.repository.CredentialAuditLogRepository;
import com.sentinelvault.repository.CredentialRepository;
import com.sentinelvault.repository.UserRepository;
import com.sentinelvault.repository.entity.CredentialAuditLogEntity;
import com.sentinelvault.repository.entity.CredentialEntity;
import com.sentinelvault.repository.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CredentialServiceTest {

    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CryptoService cryptoService;

    @Mock
    private CredentialAuditLogRepository auditLogRepository;

    @InjectMocks
    private CredentialService credentialService;

    private UserEntity user;
    private CredentialEntity credential;
    private String userEmail;
    private String ipAddress;
    private String userAgent;

    @BeforeEach
    void setUp() {
        userEmail = "lucas.teste@sentinelvault.com";
        ipAddress = "127.0.0.1";
        userAgent = "IntelliJ HTTP Client";

        user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail(userEmail);

        credential = new CredentialEntity();
        credential.setId(UUID.randomUUID());
        credential.setName("AWS key");
        credential.setPasswordPayload("encryptedSecret");
        credential.setUser(user);
    }

    @Test
    void shouldRegisterCredentialSuccessfully() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(cryptoService.encrypt("rawPassword")).thenReturn("encryptedSecret");
        when(credentialRepository.save(any(CredentialEntity.class))).thenReturn(credential);

        CredentialEntity saved = credentialService.registerCredential(
                "AWS key", "https://aws.amazon.com", "lucas.admin",
                "rawPassword", userEmail, ipAddress, userAgent
        );

        assertNotNull(saved);
        assertEquals("AWS key", saved.getName());
        verify(auditLogRepository, times(1)).save(any(CredentialAuditLogEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringAndUserNotFound() {
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () ->
                credentialService.registerCredential(
                        "AWS key", "https://aws.amazon.com", "lucas.admin",
                        "rawPassword", userEmail, ipAddress, userAgent
                )
        );

        assertEquals("User owner not found.", exception.getMessage());
        verify(credentialRepository, never()).save(any());
        verify(auditLogRepository, never()).save(any());
    }

    @Test
    void shouldFindCredentialsByUserEmailSuccessfully() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<CredentialEntity> page = new PageImpl<>(Collections.singletonList(credential), pageable, 1);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(credentialRepository.findByUserId(user.getId(), pageable)).thenReturn(page);
        when(cryptoService.decrypt("encryptedSecret")).thenReturn("rawPassword");

        Page<CredentialEntity> result = credentialService.findCredentialsByUserEmail(userEmail, pageable, ipAddress, userAgent);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("rawPassword", result.getContent().get(0).getPasswordPayload());
        verify(auditLogRepository, times(1)).save(any(CredentialAuditLogEntity.class));
    }

    @Test
    void shouldDeleteCredentialSuccessfully() {
        UUID credentialId = credential.getId();
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(credentialRepository.findById(credentialId)).thenReturn(Optional.of(credential));

        credentialService.deleteCredential(credentialId, userEmail, ipAddress, userAgent);

        verify(credentialRepository, times(1)).delete(credential);
        verify(auditLogRepository, times(1)).save(any(CredentialAuditLogEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenDeletingCredentialOfAnotherUser() {
        UUID credentialId = credential.getId();

        UserEntity alternativeUser = new UserEntity();
        alternativeUser.setId(UUID.randomUUID());
        alternativeUser.setEmail("attacker@email.com");

        credential.setUser(alternativeUser);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(credentialRepository.findById(credentialId)).thenReturn(Optional.of(credential));

        BusinessException exception = assertThrows(BusinessException.class, () ->
                credentialService.deleteCredential(credentialId, userEmail, ipAddress, userAgent)
        );

        assertEquals("You do not have permission to delete this credential.", exception.getMessage());
        verify(credentialRepository, never()).delete(any());
        verify(auditLogRepository, never()).save(any());
    }
}