ALTER TABLE curso_compra ADD COLUMN estado TINYINT(1) NOT NULL DEFAULT 1;
UPDATE curso_compra SET estado = 1;
