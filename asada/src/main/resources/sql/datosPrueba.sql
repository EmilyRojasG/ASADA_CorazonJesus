/*
  Datos de prueba para el Sistema Administrativo ASADA Barrio Corazón
  de Jesús. Ejecutar después de `creaTablas.sql`, con la base de datos
  `asada_cjesus` seleccionada.
*/

USE asada_cjesus;

-- Roles del sistema
-- 1=ADMIN, 2=SECRETARIA, 3=FONTANERO (el orden importa: se usa más abajo)
INSERT INTO rol (rol) VALUES ('ADMIN'), ('SECRETARIA'), ('FONTANERO');

-- Inserción de usuarios del sistema (personal de la ASADA)
-- Contraseñas de prueba (ver README.md):
--   admin      -> Admin.2026
--   imora      -> Imora.2026
--   fontanero  -> Fontanero.2026
-- Cambia todas antes de usar el sistema en un entorno real.
INSERT INTO usuario (username,password,nombre, apellidos, correo, telefono,ruta_imagen,activo) VALUES
('imora','$2b$10$MT01zBzii652ks.8ezWeZ.SLyngiuqmbUoMdKqHlIH5qW3AfqojL6','Irene', 'Mora Hidalgo',  'irenemora.1707@gmail.com', '8888-0000', 'https://cdn-icons-png.flaticon.com/512/149/149071.png',true),
('admin','$2b$10$ZyYxd/k5tw44KGZiaxE/Veo5BfL7kHgRVWSPUt3Ye9VyGsdL/8c6S','Junta',  'Directiva ASADA', 'asada.cjesus@gmail.com', '8888-0001','https://cdn-icons-png.flaticon.com/512/149/149071.png',true),
('fontanero','$2b$10$j6jH/HzUCTQbwhs7aCaUueJELV.Cly9u9DtJlVlPEl2WJ46kBA47q','José',  'Rodríguez Salas', 'fontanero.cjesus@gmail.com', '8888-0002','https://cdn-icons-png.flaticon.com/512/149/149071.png',true);

-- Asignación de roles a usuarios
-- admin también recibe el rol SECRETARIA para poder ver los listados
-- (abonados, categorías, lecturas), que en la tabla ruta están reservados
-- a ese rol. Si prefieres que "admin" NO vea esas pantallas, quita la
-- fila (2,2).
INSERT INTO usuario_rol (id_usuario, id_rol) VALUES (1,1),(1,2),(2,1),(2,2),(3,3);

-- Inserción de rutas con roles específicos
INSERT INTO ruta (ruta, id_rol) VALUES
('/abonado/nuevo', 1),
('/abonado/guardar', 1),
('/abonado/modificar/**', 1),
('/abonado/eliminar/**', 1),
('/categoria_tarifa/nuevo', 1),
('/categoria_tarifa/guardar', 1),
('/categoria_tarifa/modificar/**', 1),
('/categoria_tarifa/eliminar/**', 1),
('/usuario/**', 1),
('/constante/**', 1),
('/role/**', 1),
('/usuario_role/**', 1),
('/ruta/**', 1),
('/abonado/listado', 2),
('/categoria_tarifa/listado', 2),
('/lectura/**', 2),
('/reportes/**', 2),
-- Actividades del fontanero: solo el fontanero puede crear/editar/eliminar
('/actividad/nueva', 3),
('/actividad/guardar', 3),
('/actividad/modificar/**', 3),
('/actividad/eliminar/**', 3),
-- El listado de actividades y la bitácora pueden verlos tanto el
-- fontanero como el ADMIN (supervisión); al combinarse con la fila de
-- arriba, SecurityConfig arma un hasAnyRole('ADMIN','FONTANERO').
('/actividad/listado', 1),
('/actividad/listado', 3),
('/bitacora/listado', 1),
('/bitacora/listado', 3);

-- Inserción de rutas que no requieren rol
INSERT INTO ruta (ruta,requiere_rol) VALUES
('/',false),
('/index',false),
('/errores/**',false),
('/registro/**',false),
('/403',false),
('/fav/**',false),
('/js/**',false),
('/css/**',false),
('/img/**',false),
('/webjars/**',false);

-- Inserción de constantes de la aplicación
INSERT INTO constante (atributo,valor) VALUES
('dominio','localhost'),
('servidor.http','http://localhost'),
('nombre.asada','ASADA Barrio Corazón de Jesús de Acosta');

-- Inserción de categorías tarifarias de ejemplo
INSERT INTO categoria_tarifa (descripcion,tarifa_base,precio_m3,activo) VALUES
('Residencial',2500.00,350.00,true),
('Comercial',4500.00,550.00,true),
('Institucional',3800.00,450.00,true),
('Gobierno',3800.00,450.00,true);

-- Inserción de abonados de ejemplo
INSERT INTO abonado (numero_abonado,id_categoria_tarifa,nombre,apellidos,cedula,direccion,telefono,correo,numero_medidor,fecha_ingreso,ruta_imagen,activo) VALUES
('AB-0001',1,'Juan','Castro Mora','1-0234-0567','200 m norte de la iglesia, Barrio Corazón de Jesús','4556-8978','jcastro@gmail.com','MED-1001','2019-03-15',NULL,true),
('AB-0002',1,'Rebeca','Contreras Mora','1-0345-0678','Frente al salón comunal, Barrio Corazón de Jesús','5456-8789','acontreras@gmail.com','MED-1002','2020-06-01',NULL,true),
('AB-0003',2,'Pedro','Mena Loria','1-0456-0789','Calle principal, 50 m este de la escuela','7898-8936','lmena@gmail.com','MED-1003','2021-01-10',NULL,true);

-- Inserción de lecturas de ejemplo
INSERT INTO lectura (id_abonado,id_usuario,periodo_anio,periodo_mes,fecha_lectura,lectura_anterior,lectura_actual,observaciones) VALUES
(1,1,2026,5,'2026-05-28',1200.00,1215.00,NULL),
(1,1,2026,6,'2026-06-28',1215.00,1232.00,NULL),
(2,1,2026,5,'2026-05-28',800.00,809.00,NULL),
(2,1,2026,6,'2026-06-28',809.00,821.00,NULL),
(3,1,2026,5,'2026-05-28',3400.00,3455.00,'Local comercial'),
(3,1,2026,6,'2026-06-28',3455.00,3510.00,'Local comercial');
