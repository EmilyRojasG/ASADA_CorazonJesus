/*
  Actualiza una base de datos ya existente (creada con una versión anterior
  del proyecto) para dejarla al día con la última entrega, sin tener que
  recrearla desde cero:
    - Fija contraseñas conocidas para los usuarios de prueba.
    - Agrega el rol SECRETARIA al usuario admin.
    - Crea el rol FONTANERO, el usuario "fontanero" y las tablas del
      módulo de actividades del fontanero (si no existen).
    - Agrega las rutas de seguridad nuevas.

  Contraseñas resultantes (ver README.md):
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

-- Agrega el rol SECRETARIA al usuario admin (además de ADMIN), para que
-- pueda ver los listados de abonados/categorías/lecturas. Si ya la tenía
-- asignada, este INSERT no hace nada gracias al IGNORE.
INSERT IGNORE INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u, rol r
WHERE u.username = 'admin' AND r.rol = 'SECRETARIA';

-- ------------------------------------------------------------------
-- Módulo de actividades del fontanero
-- ------------------------------------------------------------------

INSERT IGNORE INTO rol (rol) VALUES ('FONTANERO');

INSERT IGNORE INTO usuario (username,password,nombre,apellidos,correo,telefono,ruta_imagen,activo)
VALUES ('fontanero','$2b$10$j6jH/HzUCTQbwhs7aCaUueJELV.Cly9u9DtJlVlPEl2WJ46kBA47q',
        'José','Rodríguez Salas','fontanero.cjesus@gmail.com','8888-0002',
        'https://cdn-icons-png.flaticon.com/512/149/149071.png', true);

INSERT IGNORE INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u, rol r
WHERE u.username = 'fontanero' AND r.rol = 'FONTANERO';

CREATE TABLE IF NOT EXISTS actividad_fontanero (
  id_actividad INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  id_abonado INT NULL,
  tipo_actividad VARCHAR(60) NOT NULL,
  descripcion VARCHAR(500) NOT NULL,
  fecha_actividad DATE NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_actividad),
  INDEX ndx_actividad_fecha (fecha_actividad),
  FOREIGN KEY fk_actividad_usuario (id_usuario) REFERENCES usuario(id_usuario),
  FOREIGN KEY fk_actividad_abonado (id_abonado) REFERENCES abonado(id_abonado)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS bitacora_actividad (
  id_bitacora INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  accion VARCHAR(20) NOT NULL,
  descripcion_actividad VARCHAR(500) NOT NULL,
  motivo VARCHAR(500) NULL,
  fecha_accion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_bitacora),
  FOREIGN KEY fk_bitacora_usuario (id_usuario) REFERENCES usuario(id_usuario)
) ENGINE = InnoDB;

-- Rutas de seguridad del nuevo módulo (evita duplicados comparando ruta+rol)
INSERT INTO ruta (ruta, id_rol)
SELECT * FROM (SELECT '/actividad/nueva' AS ruta, r.id_rol FROM rol r WHERE r.rol = 'FONTANERO') t
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = t.ruta AND id_rol = t.id_rol);

INSERT INTO ruta (ruta, id_rol)
SELECT * FROM (SELECT '/actividad/guardar' AS ruta, r.id_rol FROM rol r WHERE r.rol = 'FONTANERO') t
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = t.ruta AND id_rol = t.id_rol);

INSERT INTO ruta (ruta, id_rol)
SELECT * FROM (SELECT '/actividad/modificar/**' AS ruta, r.id_rol FROM rol r WHERE r.rol = 'FONTANERO') t
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = t.ruta AND id_rol = t.id_rol);

INSERT INTO ruta (ruta, id_rol)
SELECT * FROM (SELECT '/actividad/eliminar/**' AS ruta, r.id_rol FROM rol r WHERE r.rol = 'FONTANERO') t
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = t.ruta AND id_rol = t.id_rol);

INSERT INTO ruta (ruta, id_rol)
SELECT * FROM (SELECT '/actividad/listado' AS ruta, r.id_rol FROM rol r WHERE r.rol = 'FONTANERO') t
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = t.ruta AND id_rol = t.id_rol);

INSERT INTO ruta (ruta, id_rol)
SELECT * FROM (SELECT '/actividad/listado' AS ruta, r.id_rol FROM rol r WHERE r.rol = 'ADMIN') t
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = t.ruta AND id_rol = t.id_rol);

INSERT INTO ruta (ruta, id_rol)
SELECT * FROM (SELECT '/bitacora/listado' AS ruta, r.id_rol FROM rol r WHERE r.rol = 'FONTANERO') t
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = t.ruta AND id_rol = t.id_rol);

INSERT INTO ruta (ruta, id_rol)
SELECT * FROM (SELECT '/bitacora/listado' AS ruta, r.id_rol FROM rol r WHERE r.rol = 'ADMIN') t
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = t.ruta AND id_rol = t.id_rol);
