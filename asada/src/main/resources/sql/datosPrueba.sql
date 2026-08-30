/*
  Datos de prueba para el Sistema Administrativo ASADA Barrio Corazón
  de Jesús. Ejecutar después de `creaTablas.sql`, con la base de datos
  `asada_cjesus` seleccionada.
*/

USE asada_cjesus;

-- ------------------------------------------------------------------
-- Roles del sistema
-- ------------------------------------------------------------------
-- El sistema usa permisos por ACCIÓN (no por puesto de trabajo):
--   1=VER       -> consultar/listar información
--   2=AGREGAR   -> crear registros nuevos
--   3=EDITAR    -> modificar registros existentes
--   4=ELIMINAR  -> eliminar registros
-- Estos 4 aplican a abonados, categorías tarifarias, lecturas y usuarios.
-- Un mismo usuario puede tener cualquier combinación de estos 4 permisos.
--
--   5=FONTANERO -> permiso especial y exclusivo para administrar el
--                  módulo de "Actividades del fontanero" (crear, editar,
--                  eliminar). Se mantiene separado de los 4 anteriores
--                  a propósito, porque es un módulo operativo distinto
--                  que normalmente solo debe tocar una persona concreta
--                  (el fontanero), sin depender de si esa persona tiene
--                  o no permisos generales de EDITAR/ELIMINAR en el
--                  resto del sistema.
INSERT INTO rol (rol) VALUES ('VER'), ('AGREGAR'), ('EDITAR'), ('ELIMINAR'), ('FONTANERO');

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

-- Asignación de permisos a usuarios
-- admin e imora: control total sobre abonados/categorías/lecturas/usuarios
-- (VER + AGREGAR + EDITAR + ELIMINAR). fontanero: solo VER en lo demás,
-- más su permiso exclusivo FONTANERO para las actividades.
INSERT INTO usuario_rol (id_usuario, id_rol) VALUES
(1,1),(1,2),(1,3),(1,4),   -- imora: VER, AGREGAR, EDITAR, ELIMINAR
(2,1),(2,2),(2,3),(2,4),   -- admin: VER, AGREGAR, EDITAR, ELIMINAR
(3,1),(3,5);                -- fontanero: VER + FONTANERO

-- Inserción de rutas con permisos específicos
INSERT INTO ruta (ruta, id_rol) VALUES
-- Abonados
('/abonado/nuevo', 2),
('/abonado/guardar', 2),
('/abonado/guardar', 3),
('/abonado/modificar/**', 3),
('/abonado/eliminar/**', 4),
('/abonado/listado', 1),
-- Categorías tarifarias
('/categoria_tarifa/nuevo', 2),
('/categoria_tarifa/guardar', 2),
('/categoria_tarifa/guardar', 3),
('/categoria_tarifa/modificar/**', 3),
('/categoria_tarifa/eliminar/**', 4),
('/categoria_tarifa/listado', 1),
-- Usuarios
('/usuario/nuevo', 2),
('/usuario/guardar', 2),
('/usuario/guardar', 3),
('/usuario/modificar/**', 3),
('/usuario/eliminar/**', 4),
('/usuario/listado', 1),
-- Lecturas (registrar = agregar; no se editan, solo se registran o
-- se eliminan si están mal digitadas)
('/lectura/nueva', 2),
('/lectura/nueva', 5),
('/lectura/guardar', 2),
('/lectura/guardar', 5),
('/lectura/eliminar', 4),
('/lectura/eliminar', 5),
('/lectura/listado', 1),
('/lectura/historial/**', 1),
-- Actividades del fontanero: solo quien tenga el permiso FONTANERO
-- puede crear/editar/eliminar. El listado y la bitácora los puede ver
-- también cualquiera con permiso VER (transparencia/auditoría).
('/actividad/nueva', 5),
('/actividad/guardar', 5),
('/actividad/modificar/**', 5),
('/actividad/eliminar/**', 5),
('/actividad/listado', 1),
('/actividad/listado', 5),
('/bitacora/listado', 1),
('/bitacora/listado', 5),
-- Cartas de disponibilidad de agua
('/carta_disponibilidad/nueva', 2),
('/carta_disponibilidad/guardar', 2),
('/carta_disponibilidad/listado', 1),
('/carta_disponibilidad/ver/**', 1);
-- Nota: "/reportes/**" no requiere un permiso específico (ver más abajo,
-- en las rutas públicas/sin rol): lo puede usar cualquier usuario
-- autenticado, sin importar sus permisos.

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
INSERT INTO abonado (numero_abonado,id_categoria_tarifa,nombre,apellidos,cedula,direccion,numero_finca,telefono,correo,numero_medidor,fecha_ingreso,ruta_imagen,activo) VALUES
('AB-0001',1,'Juan','Castro Mora','1-0234-0567','200 m norte de la iglesia, Barrio Corazón de Jesús','1234',  '4556-8978','jcastro@gmail.com','MED-1001','2019-03-15',NULL,true),
('AB-0002',1,'Rebeca','Contreras Mora','1-0345-0678','Frente al salón comunal, Barrio Corazón de Jesús','5678','5456-8789','acontreras@gmail.com','MED-1002','2020-06-01',NULL,true),
('AB-0003',2,'Pedro','Mena Loria','1-0456-0789','Calle principal, 50 m este de la escuela','9012','7898-8936','lmena@gmail.com','MED-1003','2021-01-10',NULL,true);

-- Inserción de lecturas de ejemplo
INSERT INTO lectura (id_abonado,id_usuario,periodo_anio,periodo_mes,fecha_lectura,lectura_anterior,lectura_actual,observaciones) VALUES
(1,1,2026,5,'2026-05-28',1200.00,1215.00,NULL),
(1,1,2026,6,'2026-06-28',1215.00,1232.00,NULL),
(2,1,2026,5,'2026-05-28',800.00,809.00,NULL),
(2,1,2026,6,'2026-06-28',809.00,821.00,NULL),
(3,1,2026,5,'2026-05-28',3400.00,3455.00,'Local comercial'),
(3,1,2026,6,'2026-06-28',3455.00,3510.00,'Local comercial');
