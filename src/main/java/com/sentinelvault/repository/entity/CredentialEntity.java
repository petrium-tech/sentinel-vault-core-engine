package com.sentinelvault.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.util.UUID;

@Entity
@Table(name = "credentials")
// Intercepta o comando de exclusão física e o transforma em um UPDATE lógico
@SQLDelete(sql = "UPDATE credentials SET active = false WHERE id = ?")
// Filtra automaticamente as listagens e buscas para trazer apenas os registros ativos
@SQLRestriction("active = true")
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

    // Coluna que controla o estado de ativação para o Soft Delete
    @Column(nullable = false)
    private boolean active = true;
}