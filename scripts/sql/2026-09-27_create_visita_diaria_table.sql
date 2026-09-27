-- Feature: estadísticas de visitas en el panel de admin (sección Statistics).
-- Contador agregado de páginas vistas en la web pública: una fila por día, página, origen,
-- dispositivo e idioma con el número de vistas. No se guarda IP, cookies ni ningún dato que
-- identifique a la persona. origen = 'INTERNAL' son navegaciones dentro de la web; el resto
-- son entradas (primera página de una visita).
--
-- Hibernate (ddl-auto=update) la crea sola al arrancar; este script queda como historial y
-- para crearla a mano si hiciera falta. Seguro de ejecutar en cualquier momento.

CREATE TABLE IF NOT EXISTS visita_diaria (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fecha DATE NOT NULL,
    ruta VARCHAR(200) NOT NULL,
    origen VARCHAR(20) NOT NULL,
    dispositivo VARCHAR(10) NOT NULL,
    idioma VARCHAR(5) NOT NULL,
    visitas INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_visita_diaria UNIQUE (fecha, ruta, origen, dispositivo, idioma)
) ENGINE=InnoDB;
