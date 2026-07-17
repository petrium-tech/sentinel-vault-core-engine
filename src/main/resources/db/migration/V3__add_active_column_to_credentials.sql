-- Adiciona a coluna active na tabela de credenciais para controle de Soft Delete
ALTER TABLE credentials ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;