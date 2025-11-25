package util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;

public class Conexion {

    public static Connection getConnection() {
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/camvels");
            return ds.getConnection();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener conexión JNDI", e);
        }
    }
}