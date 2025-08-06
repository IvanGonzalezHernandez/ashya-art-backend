-- Eliminar tablas si existen
DROP TABLE IF EXISTS tarjeta_regalo_compra;
DROP TABLE IF EXISTS tarjeta_regalo;
DROP TABLE IF EXISTS producto_compra;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS curso_compra;
DROP TABLE IF EXISTS curso_fecha;
DROP TABLE IF EXISTS curso;
DROP TABLE IF EXISTS newsletter;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS administrador;

-- Tabla administrador
CREATE TABLE administrador (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    usuario VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Tabla cliente
CREATE TABLE cliente (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fecha_alta DATE NOT NULL,
    fecha_baja DATE,
    telefono VARCHAR(20),
    apellido VARCHAR(255) NOT NULL,
    calle VARCHAR(255) NOT NULL,
    ciudad VARCHAR(255) NOT NULL,
    codigo_postal VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    numero VARCHAR(255) NOT NULL,
    piso VARCHAR(255),
    provincia VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (email)
) ENGINE=InnoDB;

-- Tabla newsletter
CREATE TABLE newsletter (
    id BIGINT NOT NULL AUTO_INCREMENT,
    estado BIT NOT NULL,
    fecha_baja DATE,
    fecha_registro DATE NOT NULL,
    email VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (email)
) ENGINE=InnoDB;

-- Tabla curso
CREATE TABLE curso (
    id BIGINT NOT NULL AUTO_INCREMENT,
    estado BIT NOT NULL,
    fecha_baja DATE,
    precio DECIMAL(12,2) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    subtitulo VARCHAR(150),
    descripcion LONGTEXT NOT NULL,
    img1 LONGBLOB,
    img2 LONGBLOB,
    img3 LONGBLOB,
    img4 LONGBLOB,
    img5 LONGBLOB,
    nivel TEXT,
    duracion TEXT,
    piezas TEXT,
    materiales TEXT,
    plazas_maximas TINYINT UNSIGNED,
    informacion_extra TEXT,
    PRIMARY KEY (id)
) ENGINE=InnoDB;



-- Tabla curso_fecha
CREATE TABLE curso_fecha (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_curso BIGINT NOT NULL,
    fecha DATE,
    hora_inicio TIME,
    hora_fin TIME,
    plazas_disponibles INT,
    PRIMARY KEY (id),
    CONSTRAINT FK_curso_fecha_curso FOREIGN KEY (id_curso) REFERENCES curso(id)
) ENGINE=InnoDB;

-- Tabla curso_compra
CREATE TABLE curso_compra (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_cliente BIGINT NOT NULL,
    id_fecha BIGINT NOT NULL,
    plazas_reservadas INT NOT NULL,
    fecha_reserva DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_curso_compra_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id),
    CONSTRAINT FK_curso_compra_fecha FOREIGN KEY (id_fecha) REFERENCES curso_fecha(id)
) ENGINE=InnoDB;

-- Tabla producto
CREATE TABLE producto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    estado BIT NOT NULL,
    fecha_baja DATE,
    precio DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL,
    img VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    subtitulo VARCHAR(255) NOT NULL,
    descripcion TINYTEXT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- Tabla producto_compra
CREATE TABLE producto_compra (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_cliente BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    fecha_compra DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_producto_compra_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id),
    CONSTRAINT FK_producto_compra_producto FOREIGN KEY (id_producto) REFERENCES producto(id)
) ENGINE=InnoDB;

-- Tabla tarjeta_regalo
CREATE TABLE tarjeta_regalo (
    id BIGINT NOT NULL AUTO_INCREMENT,
    estado BIT NOT NULL,
    fecha_alta DATE,
    fecha_baja DATE,
    precio DECIMAL(38,2) NOT NULL,
    id_referencia VARCHAR(255) NOT NULL,
    img VARCHAR(255),
    nombre VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE (id_referencia)
) ENGINE=InnoDB;

-- Tabla tarjeta_regalo_compra
CREATE TABLE tarjeta_regalo_compra (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_cliente BIGINT NOT NULL,
    id_tarjeta BIGINT NOT NULL,
    canjeada BIT NOT NULL,
    estado BIT NOT NULL,
    fecha_baja DATE,
    fecha_caducidad DATE NOT NULL,
    fecha_compra DATE NOT NULL,
    codigo VARCHAR(255) NOT NULL,
    id_referencia VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (codigo),
    CONSTRAINT FK_tarjeta_compra_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id),
    CONSTRAINT FK_tarjeta_compra_tarjeta FOREIGN KEY (id_tarjeta) REFERENCES tarjeta_regalo(id)
) ENGINE=InnoDB;
