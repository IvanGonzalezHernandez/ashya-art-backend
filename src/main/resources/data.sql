-- Administrador
INSERT INTO administrador (email, password, usuario)
VALUES ('admin@ashya.com', '1234', 'admin');

-- Clientes
INSERT INTO cliente (fecha_alta, fecha_baja, telefono, apellido, calle, ciudad, codigo_postal, email, nombre, numero, piso, provincia)
VALUES 
  (CURDATE(), NULL, '612345678', 'González', 'Calle Mayor', 'Santander', '39001', 'cliente1@correo.com', 'Iván', '12', '3ºA', 'Cantabria');

-- Newsletter
INSERT INTO newsletter (estado, fecha_baja, fecha_registro, email)
VALUES 
  (1, NULL, CURDATE(), 'suscriptor@correo.com');

-- Cursos
INSERT INTO curso (estado, fecha_baja, precio, nombre, subtitulo, img, descripcion)
VALUES
  (1, NULL, 60.00, 'Curso de Iniciación a la Cerámica', 'Modelado básico', 'curso1.jpg', 'Curso práctico para aprender técnicas básicas de cerámica.'),
  (1, NULL, 75.00, 'Curso de Torno Cerámico', 'Uso del torno', 'curso2.jpg', 'Aprende a manejar el torno para crear piezas redondas y simétricas.'),
  (1, NULL, 80.00, 'Curso de Esmaltado', 'Técnicas de acabado', 'curso3.jpg', 'Domina técnicas de esmaltado para dar color y textura a tus piezas.'),
  (1, NULL, 90.00, 'Curso Avanzado de Modelado', 'Formas complejas', 'curso4.jpg', 'Desarrolla habilidades avanzadas para modelar figuras detalladas.'),
  (1, NULL, 50.00, 'Curso de Decoración Cerámica', 'Pintura y grabado', 'curso5.jpg', 'Aprende técnicas para decorar y personalizar tus creaciones.'),
  (1, NULL, 100.00, 'Curso de Cerámica Experimental', 'Técnicas innovadoras', 'curso6.jpg', 'Explora técnicas experimentales para crear piezas únicas.'),
  (1, NULL, 65.00, 'Curso de Joyería en Cerámica', 'Pequeñas piezas', 'curso7.jpg', 'Crea joyas y accesorios utilizando cerámica.'),
  (1, NULL, 85.00, 'Curso de Escultura en Cerámica', 'Formas artísticas', 'curso8.jpg', 'Aprende a esculpir y dar forma a piezas artísticas.'),
  (1, NULL, 70.00, 'Curso de Técnicas Mixtas', 'Cerámica y otros materiales', 'curso9.jpg', 'Combina cerámica con otros materiales para proyectos creativos.'),
  (1, NULL, 55.00, 'Curso Infantil de Cerámica', 'Creatividad para niños', 'curso10.jpg', 'Introducción a la cerámica para los más pequeños de la casa.'),
  (1, NULL, 60.00, 'Curso de Torneado y Alfarería', 'Tradición y práctica', 'curso11.jpg', 'Aprende las técnicas tradicionales de alfarería y torneado.');

-- Fechas de cursos
INSERT INTO curso_fecha (id_curso, fecha, hora_inicio, hora_fin, plazas_disponibles)
VALUES 
  (1, CURDATE() + INTERVAL 7 DAY, '10:00:00', '12:00:00', 8);

-- Compra de cursos
INSERT INTO curso_compra (id_cliente, id_fecha, plazas_reservadas, fecha_reserva)
VALUES 
  (1, 1, 2, NOW());

-- Productos
INSERT INTO producto (estado, fecha_baja, precio, stock, img, nombre, subtitulo, descripcion)
VALUES 
  (1, NULL, 25.00, 10, 'taza1.jpg', 'Taza Artesanal', 'Hecha a mano', 'Taza de cerámica blanca con detalles en azul.');

-- Compra de productos
INSERT INTO producto_compra (id_cliente, id_producto, cantidad, fecha_compra)
VALUES 
  (1, 1, 2, NOW());

-- Tarjeta regalo
INSERT INTO tarjeta_regalo (estado, fecha_alta, fecha_baja, precio, id_referencia, img, nombre)
VALUES 
  (1, CURDATE(), NULL, 50.00, 'REF123456', 'giftcard.jpg', 'Tarjeta Regalo Ashya');

-- Compra de tarjeta regalo
INSERT INTO tarjeta_regalo_compra (id_cliente, id_tarjeta, canjeada, estado, fecha_baja, fecha_caducidad, fecha_compra, codigo, id_referencia)
VALUES 
  (1, 1, 0, 1, NULL, CURDATE() + INTERVAL 6 MONTH, CURDATE(), 'CODREGALO2025', 'REF123456');