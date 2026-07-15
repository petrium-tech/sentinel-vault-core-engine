package com.sentinelvault.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "service_url")
    private String serviceUrl;

    @Column(name = "username_payload", nullable = false)
    private String usernamePayload;

    @Column(name = "password_payload", nullable = false)
    private String passwordPayload; // Este campo guardará a senha criptografada (Ciphertext)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}