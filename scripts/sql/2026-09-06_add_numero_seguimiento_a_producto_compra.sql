-- Feature: numero de seguimiento manual en compras de productos (feature/nueva-version)
-- Motivo: permitir al admin introducir el numero de seguimiento del paquete desde el
-- dashboard y enviar automaticamente un email al cliente con ese numero.
-- Aplicar manualmente en produccion antes/al desplegar esta rama.
-- Seguro de ejecutar: ADD COLUMN nullable, sin valor por defecto, no afecta filas existentes.

ALTER TABLE producto_compra ADD COLUMN numero_seguimiento VARCHAR(100) NULL;
