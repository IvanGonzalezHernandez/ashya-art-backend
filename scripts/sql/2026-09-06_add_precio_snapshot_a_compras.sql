-- Feature: guardar el precio unitario pagado en el momento de la compra (feature/nueva-version)
-- Motivo: si se edita el precio de un curso/producto/tarjeta regalo despues de una venta,
-- la linea de detalle de esa venta no tenia forma de reflejar cuanto se pago realmente,
-- ya que el precio se leia siempre en vivo desde la plantilla actual. El total de la
-- cabecera (COMPRA.total) ya quedaba fijo en el momento del pago y no se ve afectado.
-- Aplicar manualmente en produccion antes/al desplegar esta rama.
-- Seguro de ejecutar: ADD COLUMN nullable, sin valor por defecto, no afecta filas existentes.

ALTER TABLE curso_compra ADD COLUMN precio DECIMAL(12,2) NULL;
ALTER TABLE producto_compra ADD COLUMN precio DECIMAL(12,2) NULL;
ALTER TABLE tarjeta_regalo_compra ADD COLUMN precio DECIMAL(12,2) NULL;

-- Backfill best-effort de las compras ya existentes con el precio actual de la
-- plantilla asociada (no es el precio historico real si ya cambio, pero es la mejor
-- aproximacion disponible; las compras nuevas se guardaran con el precio exacto).
UPDATE curso_compra cc
JOIN curso_fecha cf ON cf.id = cc.id_fecha
JOIN curso c ON c.id = cf.id_curso
SET cc.precio = c.precio
WHERE cc.precio IS NULL;

UPDATE producto_compra pc
JOIN producto p ON p.id = pc.id_producto
SET pc.precio = p.precio
WHERE pc.precio IS NULL;

UPDATE tarjeta_regalo_compra trc
JOIN tarjeta_regalo tr ON tr.id = trc.id_tarjeta
SET trc.precio = tr.precio
WHERE trc.precio IS NULL;
