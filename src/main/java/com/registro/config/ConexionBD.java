package com.registro.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Conexión a SQL Server. Las credenciales NO están en el código: se leen, en este orden, de
 *
 *  1. Variables de entorno:       GYM_DB_URL, GYM_DB_USER, GYM_DB_PASSWORD
 *  2. Propiedades de la JVM:      -Dgym.db.url=...  -Dgym.db.user=...  -Dgym.db.password=...
 *  3. Archivo gimnasio-bd.properties (claves gym.db.url, gym.db.user, gym.db.password) buscado en:
 *       - la ruta indicada en GYM_DB_CONFIG (variable de entorno o propiedad -Dgym.db.config)
 *       - {catalina.base}/conf/gimnasio-bd.properties   (servidor Tomcat)
 *       - {carpeta del usuario}/gimnasio-bd.properties  (PC de desarrollo)
 *
 * Cada valor se busca por separado, así que se pueden combinar (por ejemplo la URL en el archivo
 * y la contraseña en una variable de entorno).
 */
public final class ConexionBD {

    private static final String NOMBRE_ARCHIVO = "gimnasio-bd.properties";

    private static volatile Properties archivo;   // se lee una sola vez

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        String url = valor("GYM_DB_URL", "gym.db.url");
        String usuario = valor("GYM_DB_USER", "gym.db.user");
        String clave = valor("GYM_DB_PASSWORD", "gym.db.password");

        if (url == null || usuario == null || clave == null) {
            StringBuilder sinPermiso = new StringBuilder();
            for (Path ruta : rutasArchivo()) {
                if (Files.exists(ruta) && !Files.isReadable(ruta)) {
                    sinPermiso.append(" El archivo ").append(ruta).append(" existe pero el usuario '")
                              .append(System.getProperty("user.name"))
                              .append("' no tiene permiso para leerlo (dele permiso de lectura).");
                }
            }
            throw new SQLException("Falta configurar la conexión a la base de datos. Defina GYM_DB_URL, "
                    + "GYM_DB_USER y GYM_DB_PASSWORD, o cree el archivo " + NOMBRE_ARCHIVO
                    + " (rutas buscadas: " + rutasArchivo() + ")." + sinPermiso);
        }

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(url, usuario, clave);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de SQL Server.", e);
        }
    }

    /**
     * Variable de entorno -> propiedad de la JVM -> archivo gimnasio-bd.properties.
     * También la usa ServicioCorreo para leer la configuración SMTP (claves gym.mail.*).
     */
    public static String valor(String variableEntorno, String clave) {
        String v = limpiar(System.getenv(variableEntorno));
        if (v == null) v = limpiar(System.getProperty(clave));
        if (v == null) v = limpiar(leerArchivo().getProperty(clave));
        return v;
    }

    /** Lee el archivo una vez; si no lo encontró (o no pudo leerlo) lo vuelve a intentar en la siguiente conexión. */
    private static Properties leerArchivo() {
        Properties p = archivo;
        if (p != null) return p;
        synchronized (ConexionBD.class) {
            if (archivo != null) return archivo;
            Properties cargado = new Properties();
            for (Path ruta : rutasArchivo()) {
                if (Files.isRegularFile(ruta)) {
                    try (InputStream in = Files.newInputStream(ruta)) {
                        cargado.load(in);
                        archivo = cargado;   // solo se guarda si se leyó bien
                        return cargado;
                    } catch (IOException ex) {
                        System.err.println("No se pudo leer " + ruta + ": " + ex);
                    }
                }
            }
            return cargado;
        }
    }

    private static List<Path> rutasArchivo() {
        List<Path> rutas = new ArrayList<>();
        String explicita = limpiar(System.getenv("GYM_DB_CONFIG"));
        if (explicita == null) explicita = limpiar(System.getProperty("gym.db.config"));
        if (explicita != null) rutas.add(Paths.get(explicita));

        String catalina = limpiar(System.getProperty("catalina.base"));
        if (catalina != null) rutas.add(Paths.get(catalina, "conf", NOMBRE_ARCHIVO));

        String home = limpiar(System.getProperty("user.home"));
        if (home != null) rutas.add(Paths.get(home, NOMBRE_ARCHIVO));
        return rutas;
    }

    private static String limpiar(String s) {
        return s == null || s.trim().isEmpty() ? null : s.trim();
    }
}
