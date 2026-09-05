# Sistema Administrativo ASADA Barrio Corazón de Jesús

Sistema de gestión administrativa para la ASADA Barrio Corazón de Jesús de
Acosta: abonados, categorías tarifarias, lecturas de medidor y reportes de
consumo de agua potable.

Proyecto propio, construido con Spring Boot, usando `tienda_vm` únicamente
como referencia de arquitectura, seguridad y buenas prácticas.

## Stack técnico

- Java 21
- Spring Boot 3.5.4
- Spring MVC + Thymeleaf
- Spring Security (autenticación y autorización dinámica basada en BD)
- Spring Data JPA + MySQL 8
- Bootstrap 5 (WebJars) + Bootstrap Icons
- Maven

## Estado del proyecto

### ✅ Entrega 1 — Base del proyecto (completa)

- Estructura del proyecto Spring Boot (Maven, Java 21, Spring Boot 3.5.4).
- Conexión con MySQL configurada (`application.properties`).
- Thymeleaf, Spring Security, Spring Data JPA y validaciones instalados.
- Bootstrap 5 vía WebJars, CSS/JS propios y logo temporal.
- Layout base, vistas de error 403/404, login y pantalla principal.

### ✅ Entrega 2 — Módulos funcionales (completa)

- **Usuarios**: CRUD completo, asignación de roles (ADMIN/SECRETARIA),
  activación/desactivación, imagen de perfil, contraseña con BCrypt
  (opcional al editar, para no forzar el cambio).
- **Categorías tarifarias**: CRUD, validaciones (descripción única,
  montos ≥ 0), estado activo/inactivo.
- **Abonados**: CRUD completo, número de abonado único, categoría
  tarifaria, datos personales, número de medidor, imagen, estado activo.
- **Lecturas**: registro con cálculo automático del consumo
  (`lectura_actual - lectura_anterior`), validación para impedir lecturas
  menores a la anterior, bloqueo de duplicados por abonado/año/mes,
  historial de lecturas por abonado.
- **Dashboard**: total de abonados, total de categorías tarifarias,
  lecturas registradas y consumo total del mes en curso.
- Seguridad dinámica basada en la tabla `ruta`: cada módulo respeta los
  roles ya definidos en `datosPrueba.sql` (ADMIN puede administrar
  usuarios/categorías/abonados; SECRETARIA puede ver listados y registrar
  lecturas; el usuario `imora` tiene ambos roles).
- Imágenes de usuarios y abonados: se guardan en disco local
  (`uploads/`, configurable con `asada.upload.dir`) y se sirven vía
  `/uploads/**`. Este servicio (`LocalStorageService`) será reemplazado
  en la Entrega 3 por uno que suba a Firebase Storage.

### ✅ Módulo adicional — Actividades del fontanero y bitácora de auditoría

- Permiso especial **FONTANERO** y usuario de prueba `fontanero`.
- **Actividades**: solo quien tenga el permiso FONTANERO puede registrar,
  editar o eliminar actividades (tipo de actividad, descripción, fecha,
  abonado relacionado opcional). Cualquier usuario con permiso VER puede
  consultarlas (solo lectura).
- Editar o eliminar una actividad exige un **motivo obligatorio** (campo
  de texto requerido) y una confirmación explícita antes de ejecutarse.
- **Bitácora de auditoría** (`/bitacora/listado`, visible para VER y
  FONTANERO): registra automáticamente cada alta, edición o eliminación
  ("fontanero ha registrado una actividad", "... ha editado ...", "... ha
  eliminado ..."), junto con el motivo cuando aplica.

### ✅ Cartas de disponibilidad de agua

- Genera constancias formales de disponibilidad de agua para futuros
  nuevos abonados (por ejemplo, para trámites de permisos de
  construcción), sin necesidad de que la persona ya esté registrada como
  abonado.
- Numeración automática y correlativa por año (`CD-2026-0001`, etc.).
- Vista imprimible que replica el machote oficial de la ASADA (mismo
  encabezado, pie de página, sello y firma), lista para "Imprimir /
  Guardar como PDF" desde el navegador (no requiere librerías
  adicionales de generación de PDF).
- Campos "Número de finca" y "Número de plano / N.° de presentación"
  son independientes y opcionales entre sí: se puede llenar solo uno,
  el otro, o ambos, según lo que tenga disponible el solicitante.
- Historial de todas las cartas emitidas, con un botón por carta para
  **adjuntar archivos de cualquier tipo** (PDF, Word, imágenes
  escaneadas, etc.), verlos y descargarlos desde el mismo listado.

### ✅ Perfil de usuario y actualización de contraseña

- **Mi perfil** (`/perfil`, cualquier usuario autenticado): muestra los
  datos básicos del usuario (nombre, usuario, roles, correo, teléfono,
  estado, foto) y permite cambiar su propia contraseña (pidiendo la
  contraseña actual).
- **Actualizar contraseña desde el login** (enlace "¿Olvidó su
  contraseña?" en `/login`): permite definir una nueva contraseña
  indicando usuario + contraseña actual, sin necesitar correo/SMTP
  configurado (no hay envío de email; es una verificación directa).

### ⏳ Entrega 3 — Pendiente

- Reportes (historial de consumo, filtros por período, exportar PDF/Excel).
- Migración de imágenes a Firebase Storage.
- Despliegue en Render con variables de entorno.
- Ajustes finales de seguridad.

## Requisitos previos

- JDK 21
- Maven 3.9+ (o usar el wrapper `mvnw` si lo agregas a tu entorno)
- MySQL 8 corriendo en `localhost:3306`

## Configuración de la base de datos local

1. Crear el esquema, el usuario de aplicación y las tablas:

   ```bash
   mysql -u root -p < src/main/resources/sql/creaTablas.sql
   ```

2. Cargar los datos de prueba (usuarios, roles, rutas, categorías tarifarias,
   abonados y lecturas de ejemplo):

   ```bash
   mysql -u root -p < src/main/resources/sql/datosPrueba.sql
   ```

   > **Importante:** la seguridad de la aplicación (`SecurityConfig`) lee la
   > tabla `ruta` al iniciar para construir las reglas de autorización, por
   > lo que **ambos scripts deben ejecutarse antes de arrancar la
   > aplicación** por primera vez.

3. Verificar que las credenciales de `src/main/resources/application.properties`
   coincidan con las creadas por el script (`usuario_prueba` /
   `Usuar1o_Clave.`). Cámbialas antes de subir el proyecto a un repositorio
   público o a producción.

## Usuarios de prueba

| Usuario | Contraseña       | Permisos             |
|------------|------------------|------------------------|
| `admin`     | `Admin.2026`     | VER, AGREGAR, EDITAR, ELIMINAR |
| `imora`     | `Imora.2026`     | VER, AGREGAR, EDITAR, ELIMINAR |
| `fontanero` | `Fontanero.2026` | VER, FONTANERO         |

> Estas contraseñas son solo para desarrollo/pruebas locales. Cámbialas
> (o crea usuarios nuevos desde el módulo de Usuarios) antes de usar el
> sistema en un entorno real.
>
> Si ya habías cargado una versión anterior de `datosPrueba.sql` en tu
> base de datos, ejecuta `src/main/resources/sql/actualizarDatosPrueba.sql`
> para poner al día contraseñas, roles, el usuario `fontanero` y las tablas
> del módulo de actividades, sin recrear toda la base.

## Ejecutar el proyecto localmente

```bash
mvn clean spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

- `http://localhost:8080/` → pantalla principal (pública).
- `http://localhost:8080/login` → inicio de sesión.

## Ejecutar las pruebas

```bash
mvn clean test
```

> El test de contexto (`@SpringBootTest`) requiere una base de datos MySQL
> accesible con las credenciales configuradas, ya que tanto el `DataSource`
> como la seguridad dinámica dependen de la conexión a `asada_cjesus`.

## Estructura del proyecto

```
src/main/java/com/asada/
├── AsadaCorazonJesusApplication.java
├── config/          # Configuración MVC (vistas, recursos)
├── controller/       # Controladores MVC
├── domain/           # Entidades JPA
├── repository/       # Repositorios Spring Data JPA
├── security/          # Configuración de Spring Security
└── service/           # Servicios (autenticación, rutas dinámicas)

src/main/resources/
├── application.properties
├── sql/               # Scripts de creación y carga de datos
├── static/            # CSS, JS e imágenes (logo)
└── templates/         # Vistas Thymeleaf (layout, páginas, errores)
```

## Módulos y permisos

El sistema usa **permisos por acción**, no roles por puesto de trabajo:
`VER`, `AGREGAR`, `EDITAR`, `ELIMINAR` (cualquier usuario puede tener
cualquier combinación de estos 4). El único permiso "especial" es
`FONTANERO`, exclusivo para administrar el módulo de Actividades.

| Módulo | Rutas | Permiso requerido |
|---|---|---|
| Dashboard, Mi perfil, Reportes | `/dashboard`, `/perfil/**`, `/reportes/**` | Cualquier usuario autenticado |
| Abonados / Categorías / Usuarios | `.../nuevo`, `.../guardar` (creación) | AGREGAR |
| Abonados / Categorías / Usuarios | `.../modificar/**`, `.../guardar` (edición) | EDITAR |
| Abonados / Categorías / Usuarios | `.../eliminar/**` | ELIMINAR |
| Abonados / Categorías / Usuarios / Lecturas | `.../listado` | VER |
| Lecturas | `/lectura/nueva`, `/lectura/guardar` | AGREGAR |
| Lecturas | `/lectura/eliminar` | ELIMINAR |
| Actividades del fontanero | `/actividad/nueva`, `/guardar`, `/modificar/**`, `/eliminar/**` | FONTANERO |
| Actividades del fontanero, Bitácora | `/actividad/listado`, `/bitacora/listado` | VER o FONTANERO |
| Cartas de disponibilidad | `/carta_disponibilidad/nueva`, `/guardar` | AGREGAR |
| Cartas de disponibilidad | `/carta_disponibilidad/*/archivos/subir` | AGREGAR |
| Cartas de disponibilidad | `/carta_disponibilidad/listado`, `/ver/**` | VER |

> Datos de prueba: `admin` e `imora` tienen los 4 permisos generales
> (VER/AGREGAR/EDITAR/ELIMINAR). `fontanero` tiene VER (para ver todo lo
> demás de solo lectura) + FONTANERO (para administrar únicamente el
> módulo de actividades).

## Despliegue en Render

El proyecto ya está preparado para desplegarse en Render como servicio
Docker:

1. **Puerto:** `server.port` lee la variable de entorno `PORT` que Render
   asigna automáticamente (con `8080` como valor por defecto local).
2. **Base de datos:** no se debe editar `application.properties` para
   producción. Basta con definir estas variables de entorno en el panel
   de Render (Spring Boot las toma automáticamente):
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `SPRING_PROFILES_ACTIVE=render` (activa `application-render.properties`,
     con caché de plantillas activada y menos ruido en los logs)
3. **Dockerfile:** compila el proyecto con Maven en una etapa y copia solo
   el `.jar` final a una imagen liviana con el JRE.
4. **`render.yaml`:** Blueprint opcional para crear el servicio con
   "New +" → "Blueprint" en Render, apuntando a este repositorio.

> ⚠️ **Almacenamiento de imágenes:** el sistema de archivos de un servicio
> web en Render es efímero — las imágenes subidas a `uploads/` se pierden
> en cada reinicio o despliegue nuevo, hasta que se migre a Firebase
> Storage (pendiente) o se agregue un "Persistent Disk" en Render.
>
> La conexión real a la base de datos en **Aiven** y a **Firebase
> Storage** se configurará en un paso posterior; por ahora el proyecto
> solo quedó *listo* para recibir esas credenciales por variables de
> entorno, sin tenerlas conectadas todavía.

## Próximos pasos (hoja de ruta)

1. Conectar la base de datos de producción en **Aiven**.
2. Migrar el almacenamiento de imágenes a **Firebase Storage**.
3. Exportación de reportes a PDF/Excel nativo (hoy exportan a CSV).
4. Ajustes finales de seguridad.
