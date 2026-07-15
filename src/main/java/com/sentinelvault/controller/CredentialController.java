package com.sentinelvault.controller;

import com.sentinelvault.controller.dto.CredentialRegistrationRequest;
import com.sentinelvault.controller.dto.CredentialResponse;
import com.sentinelvault.domain.service.CredentialService;
import com.sentinelvault.repository.entity.CredentialEntity;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/credentials")
public class CredentialController {

    private final CredentialService credentialService;

    public CredentialController(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @PostMapping
    public ResponseEntity<CredentialResponse> create(@Valid @RequestBody CredentialRegistrationRequest request) {
        CredentialEntity savedEntity = credentialService.registerCredential(
                request.getName(),
                request.getServiceUrl(),
                request.getUsernamePayload(),
                request.getPasswordPayload(),
                request.getUserId()
        );

        // Mapeamos manualmente a resposta para devolver o objeto limpo
        CredentialResponse response = new CredentialResponse(
                savedEntity.getId(),
                savedEntity.getName(),
                savedEntity.getServiceUrl(),
                savedEntity.getUsernamePayload(),
                request.getPasswordPayload() // Retornamos o texto aberto para o usuário que acabou de cadastrar
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CredentialResponse>> listByUserId(@PathVariable UUID userId) {
        java.util.List<CredentialEntity> credentials = credentialService.findCredentialsByUserId(userId);

        // Converte a lista de entidades descriptografadas para DTOs de resposta de forma limpa
        java.util.List<CredentialResponse> response = credentials.stream()
                .map(entity -> new CredentialResponse(
                        entity.getId(),
                        entity.getName(),
                        entity.getServiceUrl(),
                        entity.getUsernamePayload(),
                        entity.getPasswordPayload() // Aqui já vai o dado descriptografado
                ))
                .toList();

        return ResponseEntity.ok(response);
    }
}