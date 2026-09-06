-- Feature: las imagenes de producto/curso/tarjeta_regalo dejan de guardarse como BLOB en la
-- base de datos y pasan a guardarse como archivos en disco (carpeta "uploads", servida por el
-- backend en /uploads/**). Las columnas *_url guardan la ruta relativa al archivo.
--
-- Este cambio NO es solo SQL: hay que migrar los bytes ya guardados a archivos antes de borrar
-- las columnas BLOB. Aplicar en este orden:
--
--   1) Ejecutar el PASO 1 (ADD COLUMN) de este script. Es seguro en cualquier momento.
--   2) Desplegar el codigo de esta rama (feature/nueva-version) para que el backend ya sepa
--      leer/escribir en las columnas *_url y servir /uploads/**.
--   3) Migrar los datos: para cada fila con blob no nulo, escribir el archivo en el volumen de
--      subidas del servidor de produccion (recordar que en producción necesita persistir entre
--      despliegues, ya que si el App Service se reinicia, la carpeta "uploads" tal cual está
--      configurada en local no persiste), y hacer UPDATE de la columna *_url correspondiente con
--      la ruta resultante (p.ej. "/uploads/productos/<uuid>.webp"). En local esto se hizo con un
--      script de Node (mysql2) leyendo cada blob y escribiendo el archivo; para producción hay
--      que adaptarlo para escribir en el volumen persistente que se use (o subir a un storage
--      tipo S3 y ajustar el codigo para servir desde ahi en vez de disco local).
--   4) Una vez verificado que todas las imagenes cargan bien desde las nuevas URLs, ejecutar el
--      PASO 2 (DROP COLUMN) de este script para eliminar las columnas BLOB, ya sin uso.
--
-- No ejecutar el PASO 2 sin haber completado el PASO 3 primero: se perderian las imagenes que
-- aun no se hayan migrado a archivo.

-- ============================================================
-- PASO 1: agregar columnas de URL (seguro de ejecutar ya)
-- ============================================================
ALTER TABLE producto
  ADD COLUMN img1_url VARCHAR(255) NULL,
  ADD COLUMN img2_url VARCHAR(255) NULL,
  ADD COLUMN img3_url VARCHAR(255) NULL,
  ADD COLUMN img4_url VARCHAR(255) NULL,
  ADD COLUMN img5_url VARCHAR(255) NULL;

ALTER TABLE curso
  ADD COLUMN img1_url VARCHAR(255) NULL,
  ADD COLUMN img2_url VARCHAR(255) NULL,
  ADD COLUMN img3_url VARCHAR(255) NULL,
  ADD COLUMN img4_url VARCHAR(255) NULL,
  ADD COLUMN img5_url VARCHAR(255) NULL;

ALTER TABLE tarjeta_regalo
  ADD COLUMN img_url VARCHAR(255) NULL;

-- ============================================================
-- PASO 2: eliminar columnas BLOB (SOLO despues de migrar los datos, ver arriba)
-- ============================================================
-- ALTER TABLE producto DROP COLUMN img1, DROP COLUMN img2, DROP COLUMN img3, DROP COLUMN img4, DROP COLUMN img5;
-- ALTER TABLE curso DROP COLUMN img1, DROP COLUMN img2, DROP COLUMN img3, DROP COLUMN img4, DROP COLUMN img5;
-- ALTER TABLE tarjeta_regalo DROP COLUMN img;
