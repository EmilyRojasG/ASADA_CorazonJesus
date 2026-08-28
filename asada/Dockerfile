# ======================================================
# Dockerfile - Sistema ASADA Barrio Corazón de Jesús
# ======================================================
# Construido en dos etapas:
#   1) "build": compila el proyecto con Maven y genera el .jar.
#   2) etapa final: solo copia el .jar a una imagen liviana con el JRE,
#      sin arrastrar Maven ni el código fuente al contenedor final.
#
# Pensado para desplegarse en Render como "Web Service" de tipo Docker.

# ---------- Etapa 1: compilación ----------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Se copia primero el pom.xml para aprovechar el cache de capas de Docker:
# si solo cambia el código fuente (no las dependencias), esta capa de
# descarga de dependencias no se vuelve a ejecutar.
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Etapa 2: imagen final ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Usuario sin privilegios de root, por seguridad.
RUN addgroup -S asada && adduser -S asada -G asada

COPY --from=build /app/target/*.jar app.jar

# Carpeta para las imágenes subidas localmente (ver advertencia sobre
# almacenamiento efímero en application.properties / README).
RUN mkdir -p /app/uploads && chown -R asada:asada /app

USER asada

# Render asigna el puerto real mediante la variable de entorno PORT;
# este EXPOSE es solo informativo para quien lea el Dockerfile.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
