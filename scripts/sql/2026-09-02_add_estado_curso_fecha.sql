-- Feature: borrado lógico real para curso_fecha (feature/nueva-version)
-- Aplicar manualmente en producción antes/al desplegar esta rama.
-- Seguro de ejecutar: ADD COLUMN NOT NULL con DEFAULT 1, no afecta filas existentes
-- (todas las fechas actuales quedan activas).

ALTER TABLE curso_fecha ADD COLUMN estado TINYINT(1) NOT NULL DEFAULT 1;
