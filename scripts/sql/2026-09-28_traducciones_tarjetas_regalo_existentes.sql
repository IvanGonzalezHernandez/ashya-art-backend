-- Datos: traducciones al alemán (de) y español (es) de las tarjetas regalo existentes.
-- En tarjeta_regalo solo se traduce el nombre. Cada UPDATE comprueba id Y nombre en inglés:
-- si el id no corresponde a esa tarjeta, no se toca nada. Se puede ejecutar varias veces.
-- (No hay script para productos: en producción no había ninguno el 2026-09-28.)

SET NAMES utf8mb4;

-- 1: Ceramic Course Gift Card (building and glazing)
UPDATE tarjeta_regalo SET traducciones = '{\"de\":{\"nombre\":\"Gutschein Keramikkurs (Aufbau und Glasieren)\"},\"es\":{\"nombre\":\"Tarjeta regalo curso de cerámica (modelado y esmaltado)\"}}' WHERE id = 1 AND nombre = 'Ceramic Course Gift Card (building and glazing)';

-- 2: Ceramic Course Gift Card (building class)
UPDATE tarjeta_regalo SET traducciones = '{\"de\":{\"nombre\":\"Gutschein Keramikkurs (Aufbaukurs)\"},\"es\":{\"nombre\":\"Tarjeta regalo curso de cerámica (clase de modelado)\"}}' WHERE id = 2 AND nombre = 'Ceramic Course Gift Card (building class)';

-- 3: Gift Card
UPDATE tarjeta_regalo SET traducciones = '{\"de\":{\"nombre\":\"Gutschein\"},\"es\":{\"nombre\":\"Tarjeta regalo\"}}' WHERE id = 3 AND nombre = 'Gift Card';

