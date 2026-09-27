-- Feature: contenido en varios idiomas (cursos, productos y tarjetas regalo).
-- Las columnas de texto existentes (nombre, descripcion...) son la versión en inglés. La nueva
-- columna "traducciones" guarda el alemán y el español en JSON:
--   { "de": { "nombre": "...", "descripcion": "..." }, "es": { ... } }
-- Si un campo no está traducido, la web muestra el inglés.
--
-- Hibernate (ddl-auto=update) la crea sola al arrancar; este script queda como historial y
-- para crearla a mano si hiciera falta. Solo añade columnas: seguro de ejecutar en cualquier momento.

ALTER TABLE curso ADD COLUMN IF NOT EXISTS traducciones LONGTEXT NULL;
ALTER TABLE producto ADD COLUMN IF NOT EXISTS traducciones LONGTEXT NULL;
ALTER TABLE tarjeta_regalo ADD COLUMN IF NOT EXISTS traducciones LONGTEXT NULL;
