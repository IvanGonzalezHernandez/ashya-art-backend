-- Administrador
INSERT INTO administrador (email, password, usuario)
VALUES ('admin@ashya.com', '1234', 'admin');

-- Clientes
INSERT INTO cliente (fecha_alta, fecha_baja, telefono, apellido, calle, ciudad, codigo_postal, email, nombre, numero, piso, provincia)
VALUES 
  (CURDATE(), NULL, '612345678', 'González', 'Calle Mayor', 'Santander', '39001', 'cliente1@correo.com', 'Iván', '12', '3ºA', 'Cantabria'),
  (CURDATE(), NULL, '623456789', 'Martínez', 'Avenida Constitución', 'Torrelavega', '39300', 'cliente2@correo.com', 'Laura', '8', '1ºB', 'Cantabria'),
  (CURDATE(), NULL, '634567890', 'Pérez', 'Calle Alta', 'Castro Urdiales', '39700', 'cliente3@correo.com', 'Carlos', '5', '2ºC', 'Cantabria'),
  (CURDATE(), NULL, '645678901', 'López', 'Camino Real', 'Camargo', '39600', 'cliente4@correo.com', 'María', '3', '4ºD', 'Cantabria'),
  (CURDATE(), NULL, '656789012', 'Fernández', 'Paseo Pereda', 'Santander', '39004', 'cliente5@correo.com', 'Daniel', '10', '5ºE', 'Cantabria'),
  (CURDATE(), NULL, '667890123', 'Sánchez', 'Calle del Sol', 'Laredo', '39770', 'cliente6@correo.com', 'Lucía', '14', '6ºF', 'Cantabria'),
  (CURDATE(), NULL, '678901234', 'Ramírez', 'Avenida de España', 'Colindres', '39750', 'cliente7@correo.com', 'Javier', '1', '2ºA', 'Cantabria'),
  (CURDATE(), NULL, '689012345', 'Gómez', 'Calle Castilla', 'Reinosa', '39200', 'cliente8@correo.com', 'Elena', '6', '3ºB', 'Cantabria'),
  (CURDATE(), NULL, '690123456', 'Ruiz', 'Calle La Fuente', 'Piélagos', '39477', 'cliente9@correo.com', 'Pedro', '9', '1ºC', 'Cantabria'),
  (CURDATE(), NULL, '601234567', 'Díaz', 'Barrio El Carmen', 'Astillero', '39610', 'cliente10@correo.com', 'Ana', '4', '4ºD', 'Cantabria');


-- Newsletter
INSERT INTO newsletter (estado, fecha_baja, fecha_registro, email)
VALUES
  (1, NULL, CURDATE(), 'suscriptor1@correo.com'),
  (1, NULL, CURDATE(), 'suscriptor2@correo.com'),
  (1, NULL, CURDATE(), 'suscriptor3@correo.com'),
  (1, NULL, CURDATE(), 'suscriptor4@correo.com'),
  (1, NULL, CURDATE(), 'suscriptor5@correo.com'),
  (1, NULL, CURDATE(), 'suscriptor6@correo.com'),
  (1, NULL, CURDATE(), 'suscriptor7@correo.com'),
  (1, NULL, CURDATE(), 'suscriptor8@correo.com'),
  (1, NULL, CURDATE(), 'suscriptor9@correo.com'),
  (1, NULL, CURDATE(), 'suscriptor10@correo.com');


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
INSERT INTO curso_fecha (id_curso, fecha, hora_inicio, hora_fin, plazas_disponibles) VALUES
  (1, CURDATE() + INTERVAL 7 DAY, '10:00:00', '12:00:00', 8),
  (2, CURDATE() + INTERVAL 8 DAY, '15:00:00', '17:00:00', 10),
  (3, CURDATE() + INTERVAL 9 DAY, '09:00:00', '11:00:00', 5),
  (4, CURDATE() + INTERVAL 10 DAY, '13:00:00', '15:00:00', 7),
  (5, CURDATE() + INTERVAL 11 DAY, '16:00:00', '18:00:00', 6),
  (6, CURDATE() + INTERVAL 12 DAY, '10:00:00', '13:00:00', 4),
  (7, CURDATE() + INTERVAL 13 DAY, '14:00:00', '16:00:00', 9),
  (8, CURDATE() + INTERVAL 14 DAY, '08:00:00', '10:00:00', 8),
  (9, CURDATE() + INTERVAL 15 DAY, '17:00:00', '19:00:00', 7),
  (10, CURDATE() + INTERVAL 16 DAY, '11:00:00', '13:00:00', 5);


-- Compra de cursos
INSERT INTO curso_compra (id_cliente, id_fecha, plazas_reservadas, fecha_reserva) VALUES
  (1, 1, 2, NOW()),
  (2, 2, 1, NOW()),
  (3, 3, 3, NOW()),
  (4, 4, 1, NOW()),
  (5, 5, 2, NOW()),
  (6, 6, 1, NOW()),
  (7, 7, 2, NOW()),
  (8, 8, 1, NOW()),
  (9, 9, 3, NOW()),
  (10, 10, 1, NOW());


-- Productos
INSERT INTO producto (estado, fecha_baja, precio, stock, img, nombre, subtitulo, descripcion) VALUES
  (1, NULL, 25.00, 10, 'taza1.jpg', 'Taza Artesanal', 'Hecha a mano', 'Taza de cerámica blanca con detalles en azul.'),
  (1, NULL, 30.00, 15, 'taza2.jpg', 'Taza Rústica', 'Con acabado natural', 'Taza de cerámica con textura rústica y esmalte mate.'),
  (1, NULL, 22.50, 20, 'plato1.jpg', 'Plato Decorativo', 'Diseño único', 'Plato de cerámica pintado a mano con motivos florales.'),
  (1, NULL, 18.00, 12, 'plato2.jpg', 'Plato Vintage', 'Estilo clásico', 'Plato de cerámica con bordes decorados en tonos tierra.'),
  (1, NULL, 40.00, 8, 'jarra1.jpg', 'Jarra Grande', 'Funcional y bonita', 'Jarra de cerámica ideal para agua o jugos, acabado brillante.'),
  (1, NULL, 35.00, 7, 'jarra2.jpg', 'Jarra Pequeña', 'Compacta y elegante', 'Jarra de cerámica perfecta para leche o crema.'),
  (1, NULL, 28.00, 14, 'vaso1.jpg', 'Vaso de Cerámica', 'Colores vivos', 'Vaso hecho a mano con acabado esmaltado y colores vibrantes.'),
  (1, NULL, 26.50, 11, 'vaso2.jpg', 'Vaso Minimalista', 'Diseño limpio', 'Vaso de cerámica con líneas simples y moderno.'),
  (1, NULL, 45.00, 5, 'jarron1.jpg', 'Jarrón Grande', 'Perfecto para flores', 'Jarrón de cerámica pintado a mano para decoración floral.'),
  (1, NULL, 50.00, 6, 'jarron2.jpg', 'Jarrón Decorativo', 'Arte y función', 'Jarrón con acabado brillante y detalles artesanales.');


-- Compra de productos
INSERT INTO producto_compra (id_cliente, id_producto, cantidad, fecha_compra)
VALUES
  (1, 2, 3, NOW() - INTERVAL 10 DAY),
  (2, 4, 1, NOW() - INTERVAL 9 DAY),
  (3, 1, 5, NOW() - INTERVAL 8 DAY),
  (4, 7, 2, NOW() - INTERVAL 7 DAY),
  (5, 5, 4, NOW() - INTERVAL 6 DAY),
  (6, 3, 1, NOW() - INTERVAL 5 DAY),
  (7, 9, 3, NOW() - INTERVAL 4 DAY),
  (8, 6, 2, NOW() - INTERVAL 3 DAY),
  (9, 8, 1, NOW() - INTERVAL 2 DAY),
  (10, 10, 2, NOW() - INTERVAL 1 DAY);


-- Tarjetas regalo
INSERT INTO tarjeta_regalo (estado, fecha_alta, fecha_baja, precio, id_referencia, img, nombre) VALUES
  (1, CURDATE(), NULL, 50.00, 'REF123456', 'giftcard.jpg', 'Tarjeta Regalo Ashya'),
  (1, CURDATE(), NULL, 30.00, 'REF123457', 'giftcard_azul.jpg', 'Tarjeta Azul Ashya'),
  (1, CURDATE(), NULL, 75.00, 'REF123458', 'giftcard_dorada.jpg', 'Tarjeta Dorada Ashya'),
  (1, CURDATE(), NULL, 100.00, 'REF123459', 'giftcard_negra.jpg', 'Tarjeta Negra Ashya'),
  (1, CURDATE(), NULL, 25.00, 'REF123460', 'giftcard_simple.jpg', 'Tarjeta Básica Ashya'),
  (1, CURDATE(), NULL, 60.00, 'REF123461', 'giftcard_verde.jpg', 'Tarjeta Verde Ashya'),
  (1, CURDATE(), NULL, 45.00, 'REF123462', 'giftcard_roja.jpg', 'Tarjeta Roja Ashya'),
  (1, CURDATE(), NULL, 80.00, 'REF123463', 'giftcard_plata.jpg', 'Tarjeta Plata Ashya'),
  (1, CURDATE(), NULL, 35.00, 'REF123464', 'giftcard_rosa.jpg', 'Tarjeta Rosa Ashya'),
  (1, CURDATE(), NULL, 90.00, 'REF123465', 'giftcard_morada.jpg', 'Tarjeta Morada Ashya');


-- Compra de tarjeta regalo
INSERT INTO tarjeta_regalo_compra (id_cliente, id_tarjeta, canjeada, estado, fecha_baja, fecha_caducidad, fecha_compra, codigo, id_referencia)
VALUES 
  (1, 1, 0, 1, NULL, CURDATE() + INTERVAL 6 MONTH, CURDATE(), 'CODREGALO2025', 'REF123456');