-- Feature: tracking del importe realmente gastado al canjear una tarjeta regalo (feature/nueva-version)
-- Aplicar manualmente en producción antes/al desplegar esta rama.
-- Seguro de ejecutar: ADD COLUMN nullable, sin valor por defecto, no afecta filas existentes.

ALTER TABLE tarjeta_regalo_compra ADD COLUMN monto_utilizado DECIMAL(10,2) NULL;
