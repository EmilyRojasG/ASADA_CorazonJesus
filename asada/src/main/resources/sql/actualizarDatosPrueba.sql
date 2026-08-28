/*
  Actualiza una base de datos ya existente a la última versión del
  proyecto. Esta versión cambia el modelo de roles de "puestos de
  trabajo" (ADMIN/SECRETARIA/FONTANERO) a "permisos por acción"
  (VER/AGREGAR/EDITAR/ELIMINAR), manteniendo FONTANERO como permiso
  aparte y exclusivo para el módulo de actividades.

  IMPORTANTE: este script REEMPLAZA por completo las tablas rol, ruta y
  usuario_rol (no se pueden migrar fila por fila porque el modelo de
  permisos cambió de fondo). Los datos de abonados, categorías, lecturas
  y usuarios NO se tocan. Si habías creado usuarios adicionales a mano
  desde la pantalla de Usuarios, sus permisos se perderán con este script
  y tendrás que volver a asignárselos desde esa misma pantalla después de
  ejecutarlo.

  Contraseñas de los usuarios de prueba (ver README.md):
    admin      -> Admin.2026
    imora      -> Imora.2026
    fontanero  -> Fontanero.2026
*/

USE asada_cjesus;

UPDATE usuario
SET password = '$2b$10$ZyYxd/k5tw44KGZiaxE/Veo5BfL7kHgRVWSPUt3Ye9VyGsdL/8c6S'
WHERE username = 'admin';

UPDATE usuario
SET password = '$2b$10$MT01zBzii652ks.8ezWeZ.SLyngiuqmbUoMdKqHlIH5qW3AfqojL6'
WHERE username = 'imora';

INSERT IGNORE INTO usuario (username,password,nombre,apellidos,correo,telefono,ruta_imagen,activo)
VALUES ('fontanero','$2b$10$j6jH/HzUCTQbwhs7aCaUueJELV.Cly9u9DtJlVlPEl2WJ46kBA47q',
        'José','Rodríguez Salas','fontanero.cjesus@gmail.com','8888-0002',
        'https://cdn-icons-png.flaticon.com/512/149/149071.png', true);

-- ------------------------------------------------------------------
-- Columna nueva: número de finca del abonado
-- ------------------------------------------------------------------
SET @existe_columna := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'abonado' AND COLUMN_NAME = 'numero_finca'
);
SET @sql_alter := IF(@existe_columna = 0,
    'ALTER TABLE abonado ADD COLUMN numero_finca VARCHAR(25) AFTER direccion',
    'SELECT 1');
PREPARE stmt FROM @sql_alter;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ------------------------------------------------------------------
-- Tabla nueva: cartas de disponibilidad de agua
-- ------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS carta_disponibilidad (
  id_carta INT NOT NULL AUTO_INCREMENT,
  numero_carta VARCHAR(30) NOT NULL UNIQUE,
  id_usuario INT NOT NULL,
  nombre_solicitante VARCHAR(120) NOT NULL,
  cedula_solicitante VARCHAR(25) NOT NULL,
  direccion_propiedad VARCHAR(255) NOT NULL,
  numero_finca VARCHAR(25) NULL,
  plano_catastrado VARCHAR(60) NULL,
  fecha_emision DATE NOT NULL,
  observaciones VARCHAR(500) NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_carta),
  FOREIGN KEY fk_carta_usuario (id_usuario) REFERENCES usuario(id_usuario)
) ENGINE = InnoDB;

-- ------------------------------------------------------------------
-- Reemplazo completo del modelo de roles/rutas
-- ------------------------------------------------------------------

DELETE FROM ruta;
DELETE FROM usuario_rol;
DELETE FROM rol;
ALTER TABLE rol AUTO_INCREMENT = 1;

INSERT INTO rol (rol) VALUES ('VER'), ('AGREGAR'), ('EDITAR'), ('ELIMINAR'), ('FONTANERO');

INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol FROM usuario u, rol r
WHERE u.username IN ('admin','imora') AND r.rol IN ('VER','AGREGAR','EDITAR','ELIMINAR');

INSERT INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol FROM usuario u, rol r
WHERE u.username = 'fontanero' AND r.rol IN ('VER','FONTANERO');

INSERT INTO ruta (ruta, id_rol) VALUES
('/abonado/nuevo', 2),
('/abonado/guardar', 2),
('/abonado/guardar', 3),
('/abonado/modificar/**', 3),
('/abonado/eliminar/**', 4),
('/abonado/listado', 1),
('/categoria_tarifa/nuevo', 2),
('/categoria_tarifa/guardar', 2),
('/categoria_tarifa/guardar', 3),
('/categoria_tarifa/modificar/**', 3),
('/categoria_tarifa/eliminar/**', 4),
('/categoria_tarifa/listado', 1),
('/usuario/nuevo', 2),
('/usuario/guardar', 2),
('/usuario/guardar', 3),
('/usuario/modificar/**', 3),
('/usuario/eliminar/**', 4),
('/usuario/listado', 1),
('/lectura/nueva', 2),
('/lectura/guardar', 2),
('/lectura/eliminar', 4),
('/lectura/listado', 1),
('/lectura/historial/**', 1),
('/actividad/nueva', 5),
('/actividad/guardar', 5),
('/actividad/modificar/**', 5),
('/actividad/eliminar/**', 5),
('/actividad/listado', 1),
('/actividad/listado', 5),
('/bitacora/listado', 1),
('/bitacora/listado', 5),
('/carta_disponibilidad/nueva', 2),
('/carta_disponibilidad/guardar', 2),
('/carta_disponibilidad/listado', 1),
('/carta_disponibilidad/ver/**', 1);

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
