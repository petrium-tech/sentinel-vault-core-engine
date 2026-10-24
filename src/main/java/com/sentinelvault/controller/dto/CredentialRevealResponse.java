package com.sentinelvault.controller.dto;

import java.util.UUID;

public class CredentialRevealResponse {

    private UUID id;
    private String name;
    private String serviceUrl;
    private String usernamePayload;
    private String decryptedPassword;

    public CredentialRevealResponse() {
    }

    public CredentialRevealResponse(UUID id, String name, String serviceUrl, String usernamePayload, String decryptedPassword) {
        this.id = id;
        this.name = name;
        this.serviceUrl = serviceUrl;
        this.usernamePayload = usernamePayload;
        this.decryptedPassword = decryptedPassword;
    }

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

    public String getDecryptedPassword() {
        return decryptedPassword;
    }

    public void setDecryptedPassword(String decryptedPassword) {
        this.decryptedPassword = decryptedPassword;
    }
}
