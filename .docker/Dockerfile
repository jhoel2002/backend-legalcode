# Usa una imagen base con JRE 21 en Alpine
FROM eclipse-temurin:21-jre-alpine

# Instala fontconfig y las fuentes Dejavu necesarias
RUN apk add --no-cache fontconfig ttf-dejavu

# Create a new group with specific uid and non-root user called "admin"
# Be sure that group id are not present on host. if already exist change by arbitrary other uid
RUN addgroup -g 1028 devopsc \
    && adduser -D -G devopsc admin

# Create a new mount point at /tmp on native host because a volume is more efficient and faster than filesystem
VOLUME /tmp

# Copiamos el jar a la imagen
ARG JAR_FILE
# Establece una variable de entorno para la contraseña de la base de datos
ARG DB_URL
ARG DB_USER
ARG DB_PASSWORD
ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY

# Establece la variable de entorno DB_PASSWORD con el valor del argumento
ENV DB_URL=$DB_URL
ENV DB_USER=$DB_USER
ENV DB_PASSWORD=$DB_PASSWORD
ENV AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID
ENV AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY

# ----------  AJUSTES DE MEMORIA JVM ----------
# Limita heap a 512 MiB y arranca con 256 MiB.
# MaxRAMPercentage garantiza que, si cambias el límite del contenedor,
# la JVM nunca use más del 60 % de la RAM visible.
ENV JAVA_TOOL_OPTIONS="\
  -Xms128m -Xmx256m \
  -XX:+UseSerialGC \
  -XX:MaxRAMPercentage=60 \
  -XX:MinHeapFreeRatio=2 -XX:MaxHeapFreeRatio=5 \
  -XX:+ExitOnOutOfMemoryError"

# Copia el archivo JARgti
COPY ${JAR_FILE} /tmp/app.jar

# Change ownership of the /app directory to the "admin" user
RUN chown -R admin:devopsc /tmp

# Cambia al usuario 'admin'
USER admin

# Ejecuta el JAR (de forma limpia, sin sh -c)
ENTRYPOINT ["java", "-jar", "/tmp/app.jar"]

