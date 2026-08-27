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

- Nuevo rol **FONTANERO** y usuario de prueba `fontanero`.
- **Actividades**: solo el rol FONTANERO puede registrar, editar o
  eliminar actividades (tipo de actividad, descripción, fecha, abonado
  relacionado opcional). ADMIN puede consultarlas (solo lectura).
- Editar o eliminar una actividad exige un **motivo obligatorio** (campo
  de texto requerido) y una confirmación explícita antes de ejecutarse.
- **Bitácora de auditoría** (`/bitacora/listado`, visible para ADMIN y
  FONTANERO): registra automáticamente cada alta, edición o eliminación
  ("fontanero ha registrado una actividad", "... ha editado ...", "... ha
  eliminado ..."), junto con el motivo cuando aplica.

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

| Usuario | Contraseña       | Rol(es)             |
|------------|------------------|----------------------|
| `admin`     | `Admin.2026`     | ADMIN, SECRETARIA    |
| `imora`     | `Imora.2026`     | ADMIN, SECRETARIA    |
| `fontanero` | `Fontanero.2026` | FONTANERO            |

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

## Módulos y rutas principales

| Módulo | Rutas | Rol requerido |
|---|---|---|
| Dashboard | `GET /dashboard` | Cualquier usuario autenticado |
| Usuarios | `/usuario/**` | ADMIN |
| Categorías tarifarias | `/categoria_tarifa/nuevo`, `/guardar`, `/modificar/**`, `/eliminar/**` | ADMIN |
| Categorías tarifarias | `/categoria_tarifa/listado` | SECRETARIA |
| Abonados | `/abonado/nuevo`, `/guardar`, `/modificar/**`, `/eliminar/**` | ADMIN |
| Abonados | `/abonado/listado` | SECRETARIA |
| Lecturas | `/lectura/**` | SECRETARIA |
| Actividades del fontanero | `/actividad/nueva`, `/guardar`, `/modificar/**`, `/eliminar/**` | FONTANERO |
| Actividades del fontanero | `/actividad/listado` | ADMIN, FONTANERO |
| Bitácora de auditoría | `/bitacora/listado` | ADMIN, FONTANERO |
| Mi perfil | `/perfil`, `/perfil/cambiar-password` | Cualquier usuario autenticado |
| Actualizar contraseña | `/login/actualizar-password` | Público (verifica contraseña actual) |

> El usuario `imora` (ver datos de prueba) tiene ambos roles (ADMIN y
> SECRETARIA), por lo que puede usar el sistema completo. El usuario
> `admin` ahora también tiene ambos roles (se agregó SECRETARIA para que
> pueda ver los listados). Si prefieres que ADMIN no vea esas pantallas,
> quita esa asignación en `usuario_rol`.

## Próximos pasos (hoja de ruta)

1. **Entrega 3** – Reportes (PDF/Excel), migración de imágenes a Firebase
   Storage, despliegue en Render con variables de entorno y ajustes finales
   de seguridad.
