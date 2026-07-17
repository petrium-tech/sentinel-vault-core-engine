package com.sentinelvault.controller;

import com.sentinelvault.controller.dto.CredentialRegistrationRequest;
import com.sentinelvault.controller.dto.CredentialResponse;
import com.sentinelvault.domain.service.CredentialService;
import com.sentinelvault.repository.entity.CredentialEntity;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/credentials")
public class CredentialController {

    private final CredentialService credentialService;

    public CredentialController(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @PostMapping
    public ResponseEntity<CredentialResponse> create(
            @Valid @RequestBody CredentialRegistrationRequest request,
            @AuthenticationPrincipal String userEmail) {

        CredentialEntity savedEntity = credentialService.registerCredential(
                request.getName(),
                request.getServiceUrl(),
                request.getUsernamePayload(),
                request.getPasswordPayload(),
                userEmail
        );

        CredentialResponse response = new CredentialResponse(
                savedEntity.getId(),
                savedEntity.getName(),
                savedEntity.getServiceUrl(),
                savedEntity.getUsernamePayload(),
                request.getPasswordPayload()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CredentialResponse>> listCurrentUserCredentials(
            @AuthenticationPrincipal String userEmail,
            Pageable pageable) {

        Page<CredentialEntity> credentialsPage = credentialService.findCredentialsByUserEmail(userEmail, pageable);

        Page<CredentialResponse> responsePage = credentialsPage.map(entity -> new CredentialResponse(
                entity.getId(),
                entity.getName(),
                entity.getServiceUrl(),
                entity.getUsernamePayload(),
                entity.getPasswordPayload()
        ));

        return ResponseEntity.ok(responsePage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal String userEmail) {
        credentialService.deleteCredential(id, userEmail);
        return ResponseEntity.noContent().build();
    }
}