CREATE TABLE IF NOT EXISTS credential_audit_logs (
                                                     id UUID PRIMARY KEY,
                                                     credential_id UUID NOT NULL,
                                                     accessed_by_user_id UUID NOT NULL,
                                                     action_performed VARCHAR(50) NOT NULL,
    accessed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT fk_audit_credential FOREIGN KEY (credential_id) REFERENCES credentials(id) ON DELETE CASCADE,
    CONSTRAINT fk_audit_user FOREIGN KEY (accessed_by_user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Índice para acelerar relatórios de auditoria por credencial ou usuário
CREATE INDEX IF NOT EXISTS idx_audit_credential_id ON credential_audit_logs(credential_id);
CREATE INDEX IF NOT EXISTS idx_audit_user_id ON credential_audit_logs(accessed_by_user_id);