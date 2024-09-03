# Usar la imagen base de GraalVM con JDK 21
FROM ghcr.io/graalvm/jdk-community:21

# Etiqueta del mantenedor (cambia esto a tu información)
LABEL name=ms-books

# Definir variables de entorno, si es necesario
ENV JAVA_TOOL_OPTIONS "-Xms512m -Xmx1024m -javaagent:aws-opentelemetry-agent.jar -Dotel.javaagent.extensions=opentelemetry-extension.jar"
ENV OTEL_TRACES_SAMPLER "health"

# Especificar el directorio de trabajo
WORKDIR /opt/ms-books/

# Ejemplo: Copiar tu aplicación JAR a la imagen
COPY build/libs/*.jar tools/*.jar /opt/ms-books/

# Comando de inicio de la aplicación, ajusta esto según tus necesidades
CMD ["java","--add-opens","java.base/java.lang=ALL-UNNAMED","-jar", "api.jar"]
