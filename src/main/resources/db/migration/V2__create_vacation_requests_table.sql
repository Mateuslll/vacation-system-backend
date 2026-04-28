
CREATE TABLE vacation_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approved_by UUID,
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    
    CONSTRAINT fk_vacation_requests_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_vacation_requests_approved_by FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT chk_vacation_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')),
    CONSTRAINT chk_vacation_dates CHECK (end_date >= start_date),
    CONSTRAINT chk_vacation_rejection_reason CHECK (
        (status = 'REJECTED' AND rejection_reason IS NOT NULL) OR 
        (status != 'REJECTED')
    )
);

CREATE INDEX idx_vacation_requests_user_id ON vacation_requests(user_id);
CREATE INDEX idx_vacation_requests_status ON vacation_requests(status);
CREATE INDEX idx_vacation_requests_start_date ON vacation_requests(start_date);
CREATE INDEX idx_vacation_requests_end_date ON vacation_requests(end_date);
CREATE INDEX idx_vacation_requests_approved_by ON vacation_requests(approved_by);
CREATE INDEX idx_vacation_requests_created_at ON vacation_requests(created_at);

CREATE INDEX idx_vacation_requests_user_status ON vacation_requests(user_id, status);

CREATE INDEX idx_vacation_requests_dates ON vacation_requests(start_date, end_date);

COMMENT ON TABLE vacation_requests IS 'Tabela de solicitações de férias dos usuários';

COMMENT ON COLUMN vacation_requests.user_id IS 'ID do usuário que solicitou as férias';
COMMENT ON COLUMN vacation_requests.start_date IS 'Data de início das férias';
COMMENT ON COLUMN vacation_requests.end_date IS 'Data de término das férias';
COMMENT ON COLUMN vacation_requests.status IS 'Status da solicitação: PENDING, APPROVED, REJECTED ou CANCELLED';
COMMENT ON COLUMN vacation_requests.approved_by IS 'ID do gerente que aprovou/rejeitou a solicitação';
COMMENT ON COLUMN vacation_requests.rejection_reason IS 'Motivo da rejeição (obrigatório quando status = REJECTED)';
COMMENT ON COLUMN vacation_requests.processed_at IS 'Data/hora em que a solicitação foi processada (aprovada/rejeitada)';
