-- Feature: eliminación completa de la funcionalidad de logging de errores en BBDD (log_errores).
-- El guardado en BBDD estaba comentado en ManejadorErroresGlobal desde antes de este cambio,
-- así que la tabla no se estaba escribiendo en producción.
-- Aplicar manualmente en producción al desplegar esta rama.
--
-- Antes de ejecutar, comprobar que no hay filas (se espera 0 dado que el guardado estaba desactivado):
--   SELECT COUNT(*) FROM log_errores;

DROP TABLE IF EXISTS log_errores;
