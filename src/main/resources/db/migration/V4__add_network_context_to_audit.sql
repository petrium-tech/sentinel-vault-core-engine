-- Expansão da tabela de auditoria para capturar informações de contexto de rede
ALTER TABLE credential_audit_logs
    ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45),  -- Suporta IPv4 e IPv6 longos
    ADD COLUMN IF NOT EXISTS user_agent TEXT;         -- TEXT para suportar strings longas de navegadores/dispositivos