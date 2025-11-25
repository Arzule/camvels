FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn -B clean package

FROM tomcat:10.1-jdk17

# Copiar el certificado CA al contenedor
COPY ca.pem /usr/local/tomcat/conf/ca.pem

# Limpiar aplicaciones por defecto
RUN rm -rf /usr/local/tomcat/webapps/*

# Copiar el WAR generado en la fase de build
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

CMD ["catalina.sh", "run"]
