-- Feature: eliminación completa de la funcionalidad "Secretos" (producto tipo PDF descargable).
-- Aplicar manualmente en producción al desplegar esta rama.
--
-- Verificado (2026-09-05): tanto local como producción tienen 0 filas en ambas tablas,
-- por lo que este DROP no destruye datos.

DROP TABLE IF EXISTS secreto_compra;
DROP TABLE IF EXISTS secreto;
