package com.registro.controller;

import com.registro.dto.UsuarioDTO;
import com.registro.facade.UsuarioFACADE;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cambio de contraseña de un usuario (CambiarClave.xhtml),
 * usando UsuarioFACADE.PersonalUsuario_ActualizaClaveF.
 *
 * - Administrador: puede elegir cualquier personal con acceso al sistema.
 * - Otros roles: solo su propia contraseña.
 * - Para cambiar la propia contraseña se exige la contraseña actual.
 */
@Named("cambiarClaveBean")
@ViewScoped
public class CambiarClaveBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int LONGITUD_MINIMA = 6;

    private transient UsuarioFACADE facade;

    private List<UsuarioDTO> usuarios;
    private int personalId;
    private String claveActual;
    private String claveNueva;
    private String claveConfirmacion;

    private UsuarioFACADE getFacade() {
        if (facade == null) facade = new UsuarioFACADE();
        return facade;
    }

    @PostConstruct
    public void init() {
        UsuarioDTO yo = usuarioEnSesion();
        personalId = yo != null ? yo.getPersonal_ID() : 0;
    }

    /** Personal con usuario del sistema (solo lo usa el administrador). */
    public List<UsuarioDTO> getUsuarios() {
        if (usuarios == null) {
            usuarios = new ArrayList<>();
            try {
                for (UsuarioDTO p : getFacade().ListaPersonal()) {
                    if (p.getUsuario() != null && !p.getUsuario().trim().isEmpty()) usuarios.add(p);
                }
            } catch (RuntimeException ex) {
                usuarios = Collections.emptyList();
            }
        }
        return usuarios;
    }

    public String guardar() {
        UsuarioDTO yo = usuarioEnSesion();
        if (yo == null) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Su sesión expiró. Vuelva a iniciar sesión.");
            return null;
        }
        // Un usuario que no es administrador solo puede cambiar su propia contraseña
        if (!isAdministrador()) personalId = yo.getPersonal_ID();

        UsuarioDTO destino = getFacade().buscarPersonal(personalId);
        if (destino == null || vacio(destino.getUsuario())) {
            mensaje(FacesMessage.SEVERITY_ERROR, "El personal seleccionado no tiene usuario para ingresar al sistema.");
            return null;
        }

        if (isPropia()) {
            if (vacio(claveActual)) {
                mensaje(FacesMessage.SEVERITY_ERROR, "Ingrese su contraseña actual.");
                return null;
            }
            if (getFacade().login(destino.getUsuario().trim(), claveActual) == null) {
                mensaje(FacesMessage.SEVERITY_ERROR, "La contraseña actual no es correcta.");
                return null;
            }
        }
        if (vacio(claveNueva) || claveNueva.trim().length() < LONGITUD_MINIMA) {
            mensaje(FacesMessage.SEVERITY_ERROR, "La nueva contraseña debe tener al menos " + LONGITUD_MINIMA + " caracteres.");
            return null;
        }
        if (!claveNueva.equals(claveConfirmacion)) {
            mensaje(FacesMessage.SEVERITY_ERROR, "La confirmación no coincide con la nueva contraseña.");
            return null;
        }
        if (isPropia() && claveNueva.trim().equalsIgnoreCase(claveActual.trim())) {
            mensaje(FacesMessage.SEVERITY_ERROR, "La nueva contraseña debe ser distinta de la actual.");
            return null;
        }

        UsuarioDTO cambio = new UsuarioDTO();
        cambio.setPersonal_ID(destino.getPersonal_ID());
        cambio.setContraseña(claveNueva);
        if (!getFacade().PersonalUsuario_ActualizaClaveF(cambio)) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo actualizar la contraseña. Revise la conexión.");
            return null;
        }

        claveActual = null;
        claveNueva = null;
        claveConfirmacion = null;
        mensaje(FacesMessage.SEVERITY_INFO, "Contraseña del usuario " + destino.getUsuario().trim() + " actualizada correctamente.");
        return null;
    }

    public String cancelar() {
        return "/dashboard.xhtml?faces-redirect=true";
    }

    // ---------------------------------------------------------

    public boolean isAdministrador() {
        Object esAdmin = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("esAdmin");
        return Boolean.TRUE.equals(esAdmin);
    }

    /** El usuario está cambiando su propia contraseña (se pide la actual). */
    public boolean isPropia() {
        UsuarioDTO yo = usuarioEnSesion();
        return yo != null && yo.getPersonal_ID() == personalId;
    }

    public String getUsuarioSesion() {
        UsuarioDTO yo = usuarioEnSesion();
        Object usuario = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
        return usuario != null ? usuario.toString() : (yo != null ? yo.getNombres() : "");
    }

    public int getPersonalId() { return personalId; }
    public void setPersonalId(int personalId) { this.personalId = personalId; }
    public String getClaveActual() { return claveActual; }
    public void setClaveActual(String claveActual) { this.claveActual = claveActual; }
    public String getClaveNueva() { return claveNueva; }
    public void setClaveNueva(String claveNueva) { this.claveNueva = claveNueva; }
    public String getClaveConfirmacion() { return claveConfirmacion; }
    public void setClaveConfirmacion(String claveConfirmacion) { this.claveConfirmacion = claveConfirmacion; }

    private static UsuarioDTO usuarioEnSesion() {
        Object value = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioAutenticado");
        return value instanceof UsuarioDTO ? (UsuarioDTO) value : null;
    }
    private static boolean vacio(String s) { return s == null || s.trim().isEmpty(); }
    private static void mensaje(FacesMessage.Severity severity, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, texto, null));
    }
}
