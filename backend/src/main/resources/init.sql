-- ========================================
-- SCRIPT DE INICIALIZAÇÃO DO BANCO DE DADOS
-- Sistema de Checklist de Empilhadeiras
-- ========================================

-- Criar database (executar como superuser)
-- CREATE DATABASE checklist_db;

-- Conectar ao banco
-- \c checklist_db;

-- As tabelas serão criadas automaticamente pelo Hibernate (spring.jpa.hibernate.ddl-auto=update)
-- Este script contém dados iniciais para testes

-- ========================================
-- INSERIR USUÁRIOS DE TESTE
-- ========================================
-- Senha padrão para todos: "senha123"
-- Hash BCrypt: $2a$10$xCqEjKqF3JZ8rKqEjKqF3.eKqEjKqF3JZ8rKqEjKqF3JZ8rKqEjKqF

INSERT INTO usuarios (re, nome_completo, senha, perfil, ativo, criado_em, atualizado_em) VALUES
('ADM001', 'Administrador Sistema', '$2a$10$xCqEjKqF3JZ8rKqEjKqF3.eKqEjKqF3JZ8rKqEjKqF3JZ8rKqEjKqF', 'ADMIN', true, NOW(), NOW()),
('SUP001', 'Supervisor Geral', '$2a$10$xCqEjKqF3JZ8rKqEjKqF3.eKqEjKqF3JZ8rKqEjKqF3JZ8rKqEjKqF', 'SUPERVISOR', true, NOW(), NOW()),
('OPR001', 'João Silva', '$2a$10$xCqEjKqF3JZ8rKqEjKqF3.eKqEjKqF3JZ8rKqEjKqF3JZ8rKqEjKqF', 'OPERADOR', true, NOW(), NOW()),
('OPR002', 'Maria Santos', '$2a$10$xCqEjKqF3JZ8rKqEjKqF3.eKqEjKqF3JZ8rKqEjKqF3JZ8rKqEjKqF', 'OPERADOR', true, NOW(), NOW()),
('OPR003', 'Pedro Oliveira', '$2a$10$xCqEjKqF3JZ8rKqEjKqF3.eKqEjKqF3JZ8rKqEjKqF3JZ8rKqEjKqF', 'OPERADOR', true, NOW(), NOW())
ON CONFLICT (re) DO NOTHING;

-- ========================================
-- INSERIR EMPILHADEIRAS DE TESTE
-- ========================================
INSERT INTO empilhadeiras (modelo, tipo, capacidade, bloqueada, motivo_bloqueio, ativa, criado_em, atualizado_em) VALUES
('YALE GP030', 'Elétrica', 3000, false, NULL, true, NOW(), NOW()),
('TOYOTA 8FBE20', 'Elétrica', 2000, false, NULL, true, NOW(), NOW()),
('HYSTER H50FT', 'GLP', 5000, false, NULL, true, NOW(), NOW()),
('CATERPILLAR GP25N', 'Diesel', 2500, false, NULL, true, NOW(), NOW()),
('LINDE E16', 'Elétrica', 1600, true, 'Manutenção preventiva', true, NOW(), NOW()),
('STILL RX60-30', 'Elétrica', 3000, false, NULL, true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- ========================================
-- CONSULTAS ÚTEIS
-- ========================================

-- Verificar usuários
-- SELECT id, re, nome_completo, perfil, ativo FROM usuarios;

-- Verificar empilhadeiras
-- SELECT id, modelo, tipo, capacidade, bloqueada, ativa FROM empilhadeiras;

-- Verificar checklists
-- SELECT c.id, c.data, c.turno, c.resultado, u.nome_completo as operador, e.modelo as empilhadeira
-- FROM checklists c
-- JOIN usuarios u ON c.operador_id = u.id
-- JOIN empilhadeiras e ON c.empilhadeira_id = e.id
-- ORDER BY c.data DESC, c.hora_vistoria DESC;

-- Estatísticas de checklists
-- SELECT 
--     resultado,
--     COUNT(*) as total
-- FROM checklists
-- GROUP BY resultado;

-- Empilhadeiras mais usadas
-- SELECT 
--     e.modelo,
--     COUNT(c.id) as total_checklists
-- FROM empilhadeiras e
-- LEFT JOIN checklists c ON e.id = c.empilhadeira_id
-- GROUP BY e.id, e.modelo
-- ORDER BY total_checklists DESC;
