/*
  Script de creación de la base de datos para el Sistema Administrativo
  de la ASADA Barrio Corazón de Jesús de Acosta.

  Este script crea el esquema, el usuario de aplicación y las tablas
  necesarias para las Entregas 1 y 2 del proyecto. Ejecutarlo una única
  vez en un entorno local de desarrollo (por ejemplo con MySQL Workbench
  o `mysql -u root -p < creaTablas.sql`).
*/

-- ------------------------------------------------------------------
-- Sección de administración (ejecutar una vez en un entorno de desarrollo)
-- ------------------------------------------------------------------
DROP DATABASE IF EXISTS asada_cjesus;
DROP USER IF EXISTS 'usuario_prueba'@'%';

CREATE DATABASE asada_cjesus
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- Usuario de aplicación (coincide con application.properties)
CREATE USER 'usuario_prueba'@'%' IDENTIFIED BY 'Usuar1o_Clave.';
GRANT SELECT, INSERT, UPDATE, DELETE ON asada_cjesus.* TO 'usuario_prueba'@'%';
FLUSH PRIVILEGES;

USE asada_cjesus;

-- ------------------------------------------------------------------
-- Sección de creación de tablas
-- ------------------------------------------------------------------

-- Tabla de roles de seguridad
CREATE TABLE rol (
  id_rol INT NOT NULL AUTO_INCREMENT,
  rol VARCHAR(20) UNIQUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_rol)
) ENGINE = InnoDB;

-- Tabla de usuarios del sistema (personal administrativo de la ASADA)
CREATE TABLE usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL UNIQUE,
  password VARCHAR(512) NOT NULL,
  nombre VARCHAR(20) NOT NULL,
  apellidos VARCHAR(30) NOT NULL,
  correo VARCHAR(75) NULL UNIQUE,
  telefono VARCHAR(25) NULL,
  ruta_imagen VARCHAR(1024),
  activo BOOLEAN,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario),
  CHECK (correo REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$'),
  INDEX ndx_username (username)
) ENGINE = InnoDB;

-- Tabla de relación entre usuarios y roles
CREATE TABLE usuario_rol (
  id_usuario INT NOT NULL,
  id_rol INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario, id_rol),
  FOREIGN KEY fk_usuarioRol_usuario (id_usuario) REFERENCES usuario(id_usuario),
  FOREIGN KEY fk_usuarioRol_rol (id_rol) REFERENCES rol(id_rol)
) ENGINE = InnoDB;

-- Tabla de rutas protegidas (seguridad dinámica)
CREATE TABLE ruta (
  id_ruta INT AUTO_INCREMENT NOT NULL,
  ruta VARCHAR(255) NOT NULL,
  id_rol INT NULL,
  requiere_rol BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CHECK (id_rol IS NOT NULL OR requiere_rol = FALSE),
  PRIMARY KEY (id_ruta),
  FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
) ENGINE = InnoDB;

-- Tabla de constantes de configuración de la aplicación
CREATE TABLE constante (
  id_constante INT AUTO_INCREMENT NOT NULL,
  atributo VARCHAR(25) NOT NULL,
  valor VARCHAR(150) NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_constante),
  UNIQUE (atributo)
) ENGINE = InnoDB;

-- Tabla de categorías tarifarias
CREATE TABLE categoria_tarifa (
  id_categoria_tarifa INT NOT NULL AUTO_INCREMENT,
  descripcion VARCHAR(60) NOT NULL,
  tarifa_base DECIMAL(12,2) NOT NULL CHECK (tarifa_base >= 0),
  precio_m3 DECIMAL(12,2) NOT NULL CHECK (precio_m3 >= 0),
  activo BOOLEAN,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_categoria_tarifa),
  UNIQUE (descripcion)
) ENGINE = InnoDB;

-- Tabla de abonados
CREATE TABLE abonado (
  id_abonado INT NOT NULL AUTO_INCREMENT,
  numero_abonado VARCHAR(20) NOT NULL UNIQUE,
  id_categoria_tarifa INT NOT NULL,
  nombre VARCHAR(60) NOT NULL,
  apellidos VARCHAR(60) NOT NULL,
  cedula VARCHAR(25) NOT NULL UNIQUE,
  direccion VARCHAR(255),
  numero_finca VARCHAR(25),
  telefono VARCHAR(25),
  correo VARCHAR(75),
  numero_medidor VARCHAR(25),
  fecha_ingreso DATE,
  ruta_imagen VARCHAR(1024),
  activo BOOLEAN,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_abonado),
  INDEX ndx_numero_abonado (numero_abonado),
  INDEX ndx_cedula (cedula),
  FOREIGN KEY fk_abonado_categoria (id_categoria_tarifa) REFERENCES categoria_tarifa(id_categoria_tarifa)
) ENGINE = InnoDB;

-- Tabla de lecturas de medidor
CREATE TABLE lectura (
  id_lectura INT NOT NULL AUTO_INCREMENT,
  id_abonado INT NOT NULL,
  id_usuario INT NOT NULL,
  periodo_anio SMALLINT NOT NULL,
  periodo_mes TINYINT NOT NULL CHECK (periodo_mes BETWEEN 1 AND 12),
  fecha_lectura DATE NOT NULL,
  lectura_anterior DECIMAL(12,2) NOT NULL CHECK (lectura_anterior >= 0),
  lectura_actual DECIMAL(12,2) NOT NULL CHECK (lectura_actual >= lectura_anterior),
  observaciones VARCHAR(255),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_lectura),
  UNIQUE (id_abonado, periodo_anio, periodo_mes),
  INDEX ndx_lectura_abonado (id_abonado),
  FOREIGN KEY fk_lectura_abonado (id_abonado) REFERENCES abonado(id_abonado),
  FOREIGN KEY fk_lectura_usuario (id_usuario) REFERENCES usuario(id_usuario)
) ENGINE = InnoDB;

-- Tabla de actividades registradas por el fontanero
CREATE TABLE actividad_fontanero (
  id_actividad INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  id_abonado INT NULL,
  tipo_actividad VARCHAR(60) NOT NULL,
  descripcion VARCHAR(500) NOT NULL,
  fecha_actividad DATE NOT NULL,
  horas_trabajadas DECIMAL(5,2) NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_actividad),
  INDEX ndx_actividad_fecha (fecha_actividad),
  FOREIGN KEY fk_actividad_usuario (id_usuario) REFERENCES usuario(id_usuario),
  FOREIGN KEY fk_actividad_abonado (id_abonado) REFERENCES abonado(id_abonado)
) ENGINE = InnoDB;

-- Bitácora de auditoría: una fila por cada alta, edición o eliminación de
-- una actividad del fontanero, con el motivo cuando aplica.
CREATE TABLE bitacora_actividad (
  id_bitacora INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  accion VARCHAR(20) NOT NULL,
  descripcion_actividad VARCHAR(500) NOT NULL,
  motivo VARCHAR(500) NULL,
  fecha_accion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_bitacora),
  CHECK (accion IN ('REGISTRO', 'EDICION', 'ELIMINACION')),
  FOREIGN KEY fk_bitacora_usuario (id_usuario) REFERENCES usuario(id_usuario)
) ENGINE = InnoDB;

-- Cartas de disponibilidad de agua, para futuros nuevos abonados que
-- necesitan demostrar que la ASADA puede brindarles el servicio (por
-- ejemplo, para trámites de permisos de construcción). No requieren que
-- la persona ya sea abonado.
CREATE TABLE carta_disponibilidad (
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

-- Archivos adjuntos de una carta de disponibilidad (cualquier tipo de
-- archivo: PDF, Word, imágenes escaneadas, etc.), no solo el documento
-- generado por el sistema.
CREATE TABLE carta_archivo (
  id_archivo INT NOT NULL AUTO_INCREMENT,
  id_carta INT NOT NULL,
  id_usuario INT NOT NULL,
  nombre_original VARCHAR(255) NOT NULL,
  ruta_archivo VARCHAR(1024) NOT NULL,
  tipo_contenido VARCHAR(120) NULL,
  fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id_archivo),
  INDEX ndx_archivo_carta (id_carta),
  FOREIGN KEY fk_cartaarchivo_carta (id_carta) REFERENCES carta_disponibilidad(id_carta),
  FOREIGN KEY fk_cartaarchivo_usuario (id_usuario) REFERENCES usuario(id_usuario)
) ENGINE = InnoDB;
