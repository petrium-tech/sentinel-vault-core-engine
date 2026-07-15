package com.sentinelvault.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class CredentialRegistrationRequest {

    @NotBlank(message = "Credential name is required.")
    @Size(min = 2, max = 100, message = "Credential name must be between 2 and 100 characters.")
    private String name;

    private String serviceUrl;

    @NotBlank(message = "Username or payload identifier is required.")
    private String usernamePayload;

    @NotBlank(message = "Password or secret payload is required.")
    private String passwordPayload;

    @NotNull(message = "User owner ID is required.")
    private UUID userId; // Por enquanto, enviamos explicitamente até termos a sessão via JWT completo

    public CredentialRegistrationRequest() {
    }

    public CredentialRegistrationRequest(String name, String serviceUrl, String usernamePayload, String passwordPayload, UUID userId) {
        this.name = name;
        this.serviceUrl = serviceUrl;
        this.usernamePayload = usernamePayload;
        this.passwordPayload = passwordPayload;
        this.userId = userId;
    }

    // Getters e Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getUsernamePayload() {
        return usernamePayload;
    }

    public void setUsernamePayload(String usernamePayload) {
        this.usernamePayload = usernamePayload;
    }

    public String getPasswordPayload() {
        return passwordPayload;
    }

    public void setPasswordPayload(String passwordPayload) {
        this.passwordPayload = passwordPayload;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}