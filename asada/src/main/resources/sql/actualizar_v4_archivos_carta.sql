/*
  Script incremental — SOLO agrega lo necesario para adjuntar archivos a
  las cartas de disponibilidad (subir/ver/descargar archivos de
  cualquier tipo por cada carta).

  No toca ninguna otra tabla ni la configuración de conexión. Es seguro
  ejecutarlo más de una vez.

  Ejecútalo contra la base de datos que estés usando, seleccionándola
  primero con USE, por ejemplo:
    USE asada_cjesus;
*/

CREATE TABLE IF NOT EXISTS carta_archivo (
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

INSERT INTO ruta (ruta, id_rol)
SELECT * FROM (SELECT '/carta_disponibilidad/*/archivos/subir' AS ruta, r.id_rol FROM rol r WHERE r.rol = 'AGREGAR') t
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = t.ruta AND id_rol = t.id_rol);
