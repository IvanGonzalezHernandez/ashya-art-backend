-- Feature: recogida en el taller (0 euros) o envio a Alemania/UE en el checkout (feature/nueva-version)
-- Motivo: guardar que metodo de entrega eligio el cliente para cada compra de producto,
-- para que el admin sepa si prepararlo para recogida o para envio (y a que zona).
-- Aplicar manualmente en produccion antes/al desplegar esta rama.
-- Seguro de ejecutar: ADD COLUMN nullable, sin valor por defecto, no afecta filas existentes.

ALTER TABLE producto_compra ADD COLUMN metodo_envio VARCHAR(20) NULL;
