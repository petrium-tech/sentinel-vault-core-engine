package com.sentinelvault.controller;

import com.sentinelvault.controller.dto.CredentialRegistrationRequest;
import com.sentinelvault.controller.dto.CredentialResponse;
import com.sentinelvault.domain.service.CredentialService;
import com.sentinelvault.repository.entity.CredentialEntity;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @AuthenticationPrincipal String userEmail) { // Injeção automática via Spring Security

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
                request.getPasswordPayload() // Retorna o texto aberto confirmado no ato da criação
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CredentialResponse>> listCurrentUserCredentials(@AuthenticationPrincipal String userEmail) {
        List<CredentialEntity> credentials = credentialService.findCredentialsByUserEmail(userEmail);

        List<CredentialResponse> response = credentials.stream()
                .map(entity -> new CredentialResponse(
                        entity.getId(),
                        entity.getName(),
                        entity.getServiceUrl(),
                        entity.getUsernamePayload(),
                        entity.getPasswordPayload() // Dado descriptografado on-the-fly
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}