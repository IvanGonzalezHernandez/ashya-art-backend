-- Feature: configuración de mantenimiento controlable desde el panel de admin (Utils).
-- Aplicar manualmente en producción al desplegar esta rama.
--
-- Nota: con spring.jpa.hibernate.ddl-auto=update, Hibernate crea esta tabla solo con
-- arrancar el backend, así que este script es más una constancia del cambio de esquema
-- que un paso estrictamente necesario. Se incluye igualmente para mantener el historial
-- completo de cambios de esquema en esta carpeta.

CREATE TABLE IF NOT EXISTS configuracion_sitio (
    id BIGINT NOT NULL,
    mantenimiento_activo BIT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;
