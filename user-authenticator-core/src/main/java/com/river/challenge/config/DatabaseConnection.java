package com.river.challenge.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Provee conexiones JDBC a PostgreSQL.
 *
 * La configuración se resuelve en este orden de prioridad:
 *   1. Variables de entorno (DB_URL, DB_USER, DB_PASSWORD)
 *   2. Archivo db.properties en el classpath
 *   3. Valores por defecto (desarrollo local)
 *
 * De esta forma las credenciales no quedan embebidas en el código fuente.
 */
public class DatabaseConnection {

    private static final String url;
    private static final String user;
    private static final String password;

    static {
        Properties props = new Properties();
        try (InputStream in = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar db.properties, se usarán valores por defecto: "
                    + e.getMessage());
        }

        url = resolve(props, "db.url", "DB_URL", "jdbc:postgresql://localhost:5432/user_management");
        user = resolve(props, "db.user", "DB_USER", "postgres");
        password = resolve(props, "db.password", "DB_PASSWORD", "admin123");

        // Registro explícito del driver: garantiza que DriverManager lo encuentre
        // cuando el JAR vive en WEB-INF/lib de un contenedor como Tomcat.
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver de PostgreSQL no encontrado en el classpath: " + e.getMessage());
        }
    }

    private static String resolve(Properties props, String propKey, String envKey, String defaultValue) {
        String fromEnv = System.getenv(envKey);
        if (fromEnv != null && !fromEnv.trim().isEmpty()) {
            return fromEnv;
        }
        return props.getProperty(propKey, defaultValue);
    }

    private DatabaseConnection() {
        // Clase de utilidad: no instanciable
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
