-- Inserção dos usuários
INSERT INTO users (id, name, login, password, type, is_blocked)
VALUES
    ('f00dc5df-b266-42ba-acc6-08d7256f9385', 'Administrador', 'admin', '$2a$10$7n6JDVP/iiPLk6Ro3W2jBuz8CqHD34At1w1l5P8AGh6ph3MNNwkGC', 'ADMIN', false),
    ('ba45d038-47aa-4877-996b-3d9603a53f51', 'Frederico', 'fred', '$2a$10$BZm4sInLcc.vNHHf8x2gG.gUUohxpKjcNTKsj7mklrVqC6VLQOKNq', 'NORMAL', false)
    ON CONFLICT (id) DO NOTHING;


-- Inserção das categorias predefinidas

-- ENTRADA
INSERT INTO category (id, name, nature, active, order_index) VALUES
                                                                 ('1347e2ee-c08a-4713-a59a-71bb45120798', 'Salário', 'ENTRADA', true, 1),
                                                                 ('835f337b-7b7c-4936-85ed-780a6707f203', 'Cashback', 'ENTRADA', true, 2),
                                                                 ('79844388-5e13-432d-88ab-c63a3983b3ac', 'Resgate Investimento', 'ENTRADA', true, 3),
                                                                 ('eb501206-8876-415c-b629-6112214a1b7d', 'Outras Entradas', 'ENTRADA', true, 99)
    ON CONFLICT (id) DO NOTHING;

-- SAÍDA
INSERT INTO category (id, name, nature, active, order_index) VALUES
                                                                 ('d1fb07b8-42b4-4730-a804-7dea85b0dfd1', 'Saúde e Remédios', 'SAIDA', true, 1),
                                                                 ('dcdbe22f-8343-4623-beb8-90844613ef2e', 'Academia e Personal', 'SAIDA', true, 2),
                                                                 ('bf558a0b-ecb7-414e-93f0-0f9f022374e5', 'Carros e Uber', 'SAIDA', true, 3),
                                                                 ('eea83ac4-8964-48cd-9ad7-fc241189af0e', 'Educação e Cursos', 'SAIDA', true, 4),
                                                                 ('09854890-072e-477a-8a12-88db412bc0f8', 'Lazer e Turismo', 'SAIDA', true, 5),
                                                                 ('1f66bb7d-b8b8-457a-b01c-4efba91347a5', 'Condomínio', 'SAIDA', true, 6),
                                                                 ('f9691d0f-6803-4f39-a220-485e39715caf', 'Energia', 'SAIDA', true, 7),
                                                                 ('42014449-0836-4b89-b07b-0c03f256a275', 'Celular', 'SAIDA', true, 8),
                                                                 ('e567334d-43f0-4b17-8b25-595f639e358c', 'Internet', 'SAIDA', true, 9),
                                                                 ('e8cce625-3ca9-40ac-9e20-7fd6de9f8c4d', 'Itens Pessoais', 'SAIDA', true, 10),
                                                                 ('2cf087ea-6c20-40f7-844a-193968264c0f', 'Feira', 'SAIDA', true, 11),
                                                                 ('9b960966-0b5e-433d-8d7c-e686f502e611', 'Casa', 'SAIDA', true, 12),
                                                                 ('602655f6-f99e-4012-aecd-2a6d0e722392', 'Impostos', 'SAIDA', true, 13),
                                                                 ('cd081acb-b6d7-4dfc-852e-85eb2e0d013f', 'Outros gastos', 'SAIDA', true, 99)
    ON CONFLICT (id) DO NOTHING;

-- INVESTIMENTO
INSERT INTO category (id, name, nature, active, order_index) VALUES
                                                                 ('5767af2b-a2f1-4c8c-8dc4-ffd5f0563866', 'Aporte Renda Fixa', 'INVESTIMENTO', true, 1),
                                                                 ('3479f438-1597-424b-ab2c-985a4d52941d', 'Aporte Renda Variável', 'INVESTIMENTO', true, 2),
                                                                 ('6d49c5af-fac7-47a7-911b-1d28487b3672', 'Aporte Reserva Emergencia', 'INVESTIMENTO', true, 3),
                                                                 ('725e7eea-52d7-4fa9-b7be-cfbe8fbbb336', 'Aporte Previdência', 'INVESTIMENTO', true, 4)
    ON CONFLICT (id) DO NOTHING;
