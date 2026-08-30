/*
  Script incremental — SOLO agrega lo necesario para:
    1) El campo "horas trabajadas" en el registro de actividades del
       fontanero.
    2) Permiso para que el usuario con rol FONTANERO también pueda
       registrar y eliminar lecturas (además de seguir administrando
       actividades).

  No toca la configuración de conexión ni ninguna otra tabla. Es seguro
  ejecutarlo más de una vez (usa IF NOT EXISTS / comprobaciones antes de
  insertar).

  Ejecútalo contra la base de datos que estés usando (local o la que
  tengas en Aiven), seleccionándola primero con USE, por ejemplo:
    USE asada_cjesus;
*/

-- ------------------------------------------------------------------
-- 1) Columna "horas trabajadas" en actividad_fontanero
-- ------------------------------------------------------------------
SET @existe_columna := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'actividad_fontanero'
      AND COLUMN_NAME = 'horas_trabajadas'
);
SET @sql_alter := IF(@existe_columna = 0,
    'ALTER TABLE actividad_fontanero ADD COLUMN horas_trabajadas DECIMAL(5,2) NULL AFTER fecha_actividad',
    'SELECT 1');
PREPARE stmt FROM @sql_alter;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ------------------------------------------------------------------
-- 2) El fontanero también puede registrar y eliminar lecturas
-- ------------------------------------------------------------------
INSERT INTO ruta (ruta, id_rol)
SELECT * FROM (SELECT '/lectura/nueva' AS ruta, r.id_rol FROM rol r WHERE r.rol = 'FONTANERO') t
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = t.ruta AND id_rol = t.id_rol);

INSERT INTO ruta (ruta, id_rol)
SELECT * FROM (SELECT '/lectura/guardar' AS ruta, r.id_rol FROM rol r WHERE r.rol = 'FONTANERO') t
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = t.ruta AND id_rol = t.id_rol);

INSERT INTO ruta (ruta, id_rol)
SELECT * FROM (SELECT '/lectura/eliminar' AS ruta, r.id_rol FROM rol r WHERE r.rol = 'FONTANERO') t
WHERE NOT EXISTS (SELECT 1 FROM ruta WHERE ruta = t.ruta AND id_rol = t.id_rol);
