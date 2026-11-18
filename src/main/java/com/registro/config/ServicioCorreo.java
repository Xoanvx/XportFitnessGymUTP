package com.registro.config;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Envío de correos por SMTP. La configuración NO está en el código: se lee igual que la conexión
 * a la base de datos (variables de entorno, propiedades de la JVM o el archivo gimnasio-bd.properties):
 *
 *   GYM_MAIL_HOST      / gym.mail.host       servidor SMTP (p. ej. mail.sisdataperu.com)
 *   GYM_MAIL_PORT      / gym.mail.port       puerto (587 por defecto; 465 para SSL)
 *   GYM_MAIL_USER      / gym.mail.user       cuenta que envía
 *   GYM_MAIL_PASSWORD  / gym.mail.password   contraseña de esa cuenta
 *   GYM_MAIL_FROM      / gym.mail.from       remitente (por defecto el mismo usuario)
 *   GYM_MAIL_SSL       / gym.mail.ssl        true = SSL directo (465); false = STARTTLS (587)
 */
public final class ServicioCorreo {

    private static final String NOMBRE_REMITENTE = "Xport Fitness Gym";

    private ServicioCorreo() {
    }

    /** true si están configurados al menos el servidor, el usuario y la contraseña SMTP. */
    public static boolean estaConfigurado() {
        return ConexionBD.valor("GYM_MAIL_HOST", "gym.mail.host") != null
                && ConexionBD.valor("GYM_MAIL_USER", "gym.mail.user") != null
                && ConexionBD.valor("GYM_MAIL_PASSWORD", "gym.mail.password") != null;
    }

    /** Envía un correo HTML. Lanza MessagingException si el servidor SMTP lo rechaza. */
    public static void enviar(String para, String asunto, String html) throws MessagingException {
        String host = ConexionBD.valor("GYM_MAIL_HOST", "gym.mail.host");
        String usuario = ConexionBD.valor("GYM_MAIL_USER", "gym.mail.user");
        String clave = ConexionBD.valor("GYM_MAIL_PASSWORD", "gym.mail.password");
        if (host == null || usuario == null || clave == null) {
            throw new MessagingException("Falta configurar el correo: defina gym.mail.host, gym.mail.user "
                    + "y gym.mail.password en gimnasio-bd.properties (o GYM_MAIL_HOST, GYM_MAIL_USER, GYM_MAIL_PASSWORD).");
        }
        String puerto = ConexionBD.valor("GYM_MAIL_PORT", "gym.mail.port");
        String remitente = ConexionBD.valor("GYM_MAIL_FROM", "gym.mail.from");
        boolean ssl = "true".equalsIgnoreCase(ConexionBD.valor("GYM_MAIL_SSL", "gym.mail.ssl"));
        if (puerto == null) puerto = ssl ? "465" : "587";
        if (remitente == null) remitente = usuario;

        Properties p = new Properties();
        p.put("mail.smtp.host", host);
        p.put("mail.smtp.port", puerto);
        p.put("mail.smtp.auth", "true");
        if (ssl) {
            p.put("mail.smtp.ssl.enable", "true");
        } else {
            p.put("mail.smtp.starttls.enable", "true");
        }
        p.put("mail.smtp.connectiontimeout", "15000");
        p.put("mail.smtp.timeout", "15000");
        p.put("mail.smtp.writetimeout", "15000");

        Session sesion = Session.getInstance(p, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, clave);
            }
        });

        MimeMessage mensaje = new MimeMessage(sesion);
        try {
            mensaje.setFrom(new InternetAddress(remitente, NOMBRE_REMITENTE, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            mensaje.setFrom(new InternetAddress(remitente));
        }
        mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(para, true));
        mensaje.setSubject(asunto, "UTF-8");
        mensaje.setContent(html, "text/html; charset=UTF-8");
        Transport.send(mensaje);
    }
}
