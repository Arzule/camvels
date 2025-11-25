FROM tomcat:10-jdk22

# Borra las apps por defecto de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copia tu WAR y lo nombra ROOT.war para que sea la aplicación principal
COPY target/CamvelsInventario.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

CMD ["catalina.sh", "run"]