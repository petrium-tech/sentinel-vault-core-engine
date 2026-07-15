package com.sentinelvault.controller.dto;

import java.util.UUID;

public class CredentialResponse {

    private UUID id;
    private String name;
    private String serviceUrl;
    private String usernamePayload;
    private String passwordPayload; // Este campo conterá o dado descriptografado sob demanda

    public CredentialResponse() {
    }

    public CredentialResponse(UUID id, String name, String serviceUrl, String usernamePayload, String passwordPayload) {
        this.id = id;
        this.name = name;
        this.serviceUrl = serviceUrl;
        this.usernamePayload = usernamePayload;
        this.passwordPayload = passwordPayload;
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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
}