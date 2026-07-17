-- Criação da tabela de Usuários (users) com ID baseado em UUID nativo
CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY,
                                     full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
                             );

-- Criação de índice de busca rápida para login por e-mail
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Criação da tabela de Credenciais (credentials) do Cofre
CREATE TABLE IF NOT EXISTS credentials (
                                           id UUID PRIMARY KEY,
                                           name VARCHAR(100) NOT NULL,
    service_url VARCHAR(255),
    username_payload VARCHAR(150) NOT NULL,
    password_payload TEXT NOT NULL, -- TEXT para comportar strings criptografadas longas
    user_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_credentials_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Criação de índice para otimização da consulta de chaves estrangeiras
CREATE INDEX IF NOT EXISTS idx_credentials_user_id ON credentials(user_id);