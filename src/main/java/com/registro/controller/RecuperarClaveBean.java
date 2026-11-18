package com.registro.controller;

import com.registro.config.ServicioCorreo;
import com.registro.dto.UsuarioDTO;
import com.registro.facade.UsuarioFACADE;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * "¿Has olvidado tu clave?" en login.xhtml.
 *
 * Las contraseñas se guardan como hash SHA-256, así que la clave actual no se puede recuperar:
 * se genera una clave temporal, se envía al correo registrado del personal y se guarda
 * (encriptada) en su lugar. El usuario la cambia luego en "Cambiar contraseña".
 */
@Named("recuperarClaveBean")
@RequestScoped
public class RecuperarClaveBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Pattern FORMATO_CORREO =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    /** Sin O/0 ni I/1 para que no se confundan al leerla. La encriptación ignora mayúsculas. */
    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LARGO_CLAVE = 8;
    /** Tiempo mínimo entre dos solicitudes para el mismo correo. */
    private static final long ESPERA_MS = 5 * 60 * 1000L;
    private static final Map<String, Long> ULTIMA_SOLICITUD = new ConcurrentHashMap<>();
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private static final String MENSAJE_ENVIADO =
            "Si el correo está registrado, recibirás una clave temporal en unos minutos. "
            + "Revisa también la carpeta de spam.";

    private String correo;
    private String mensaje;
    private String error;
    private boolean mostrar;

    public String recuperar() {
        mostrar = true;
        mensaje = null;
        error = null;

        String c = correo == null ? "" : correo.trim();
        if (!FORMATO_CORREO.matcher(c).matches()) {
            error = "Ingrese un correo electrónico válido.";
            return null;
        }
        if (!ServicioCorreo.estaConfigurado()) {
            error = "La recuperación por correo no está disponible. Consulta en recepción.";
            return null;
        }

        String clave = c.toLowerCase(Locale.ROOT);
        long ahora = System.currentTimeMillis();
        Long anterior = ULTIMA_SOLICITUD.get(clave);
        if (anterior != null && ahora - anterior < ESPERA_MS) {
            // Mismo mensaje que un envío normal: no se revela si el correo existe.
            mensaje = MENSAJE_ENVIADO;
            return null;
        }
        ULTIMA_SOLICITUD.put(clave, ahora);

        UsuarioFACADE facade = new UsuarioFACADE();
        List<UsuarioDTO> personal = facade.ListaPersonal();
        for (UsuarioDTO p : personal) {
            if (p.getCorreo() == null || !p.getCorreo().trim().equalsIgnoreCase(c)) continue;
            if (p.getUsuario() == null || p.getUsuario().isBlank()) continue;
            if ("Inactivo".equalsIgnoreCase(p.getEstadoGene())) continue;
            restablecer(facade, p, c);
        }

        // El mensaje es el mismo exista o no el correo, para no revelar qué correos están registrados.
        mensaje = MENSAJE_ENVIADO;
        correo = null;
        return null;
    }

    /** Envía primero el correo y solo si salió bien cambia la clave, para no dejar al usuario sin acceso. */
    private void restablecer(UsuarioFACADE facade, UsuarioDTO p, String destino) {
        String temporal = generarClave();
        try {
            ServicioCorreo.enviar(destino, "Recuperación de clave - Xport Fitness Gym",
                    plantillaCorreo(p, temporal));
        } catch (Exception ex) {
            System.err.println("No se pudo enviar el correo de recuperación al Personal_ID "
                    + p.getPersonal_ID() + ": " + ex);
            return;
        }

        UsuarioDTO cambio = new UsuarioDTO();
        cambio.setPersonal_ID(p.getPersonal_ID());
        cambio.setContraseña(temporal);
        if (!facade.PersonalUsuario_ActualizaClaveF(cambio)) {
            System.err.println("Se envió la clave temporal pero no se pudo grabar para el Personal_ID "
                    + p.getPersonal_ID());
        }
    }

    private static String generarClave() {
        StringBuilder sb = new StringBuilder(LARGO_CLAVE);
        for (int i = 0; i < LARGO_CLAVE; i++) {
            sb.append(CARACTERES.charAt(ALEATORIO.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }

    private static String plantillaCorreo(UsuarioDTO p, String temporal) {
        String nombre = escapar(p.getNombres() == null ? "" : p.getNombres().trim());
        String usuario = escapar(p.getUsuario().trim());
        return "<div style=\"font-family:Arial,sans-serif;font-size:14px;color:#17233C;max-width:520px\">"
                + "<h2 style=\"color:#2864F0;margin:0 0 16px\">Xport Fitness Gym</h2>"
                + "<p>Hola " + nombre + ",</p>"
                + "<p>Recibimos una solicitud para recuperar el acceso al sistema. Estos son tus datos:</p>"
                + "<table style=\"border-collapse:collapse;margin:12px 0\">"
                + "<tr><td style=\"padding:6px 12px 6px 0;color:#64748B\">Usuario</td><td style=\"padding:6px 0\"><b>"
                + usuario + "</b></td></tr>"
                + "<tr><td style=\"padding:6px 12px 6px 0;color:#64748B\">Clave temporal</td>"
                + "<td style=\"padding:6px 0;font-family:Consolas,monospace;font-size:18px;letter-spacing:2px\"><b>"
                + temporal + "</b></td></tr></table>"
                + "<p>Ingresa con esta clave y cámbiala de inmediato desde <b>Cambiar contraseña</b> en el menú.</p>"
                + "<p style=\"color:#64748B;font-size:12px\">Si no solicitaste este cambio, avisa en recepción o al "
                + "administrador del sistema.</p></div>";
    }

    private static String escapar(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getMensaje() { return mensaje; }
    public String getError() { return error; }
    public boolean isMostrar() { return mostrar; }
}
