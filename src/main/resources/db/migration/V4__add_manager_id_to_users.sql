
ALTER TABLE users
ADD COLUMN manager_id UUID;

ALTER TABLE users
ADD CONSTRAINT fk_users_manager_id
FOREIGN KEY (manager_id) REFERENCES users(id)
ON DELETE SET NULL;

CREATE INDEX idx_users_manager_id ON users(manager_id);

COMMENT ON COLUMN users.manager_id IS 'UUID do manager responsável por este usuário (colaborador). NULL para ADMIN ou managers sem supervisor.';
COMMENT ON INDEX idx_users_manager_id IS 'Índice para otimizar buscas de colaboradores por manager_id';
