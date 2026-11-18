package com.registro.controller;

import com.registro.dto.MembresiaDTO;
import com.registro.dto.UsuarioDTO;
import com.registro.facade.MembresiaFACADE;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Controlador de vista JSF para los planes de membresía (listado y registro), usando MembresiaFACADE. */
@Named("membresiaBean")
@SessionScoped
public class MembresiaBean implements Serializable {
    private static final long serialVersionUID = 1L;
    static final String COD_EMP_DEFECTO = "01";
    private static final String VISTA_LISTA = "/ListaPlanesMembresia.xhtml?faces-redirect=true";

    private transient MembresiaFACADE facade = new MembresiaFACADE();

    private MembresiaFACADE getFacade() {
        if (facade == null) facade = new MembresiaFACADE();
        return facade;
    }

    private List<MembresiaDTO> listaPlanes;
    private List<MembresiaDTO> listaFrecuencias;
    private String filtro;

    // Formulario de registro / edición
    private MembresiaDTO plan;
    private String modo = "grabar";
    private String codigoParam;

    // ---------------------------------------------------------
    // LISTADO DE PLANES
    // ---------------------------------------------------------

    /** Todos los planes registrados (sp_ListaMembresia). */
    public List<MembresiaDTO> getListaPlanes() {
        if (listaPlanes == null) {
            try { listaPlanes = getFacade().ListaMembresiaF(); }
            catch (RuntimeException ex) { listaPlanes = Collections.emptyList(); }
        }
        return listaPlanes;
    }

    /** Planes filtrados por código, nombre, frecuencia o estado. */
    public List<MembresiaDTO> getPlanesFiltrados() {
        return filtrar(getListaPlanes(), filtro);
    }

    /** Filtro compartido por la vista y el reporte PDF. */
    public static List<MembresiaDTO> filtrar(List<MembresiaDTO> todos, String filtro) {
        if (vacio(filtro)) return todos;
        String texto = filtro.trim().toLowerCase(Locale.ROOT);
        List<MembresiaDTO> resultado = new ArrayList<>();
        for (MembresiaDTO p : todos) {
            if (contiene(p.getCodigoM(), texto) || contiene(p.getNombreMembresia(), texto)
                    || contiene(p.getFrecuencia(), texto) || contiene(p.getEstado(), texto)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public int getTotalPlanes() { return getListaPlanes().size(); }

    /** Frecuencias para el combo del formulario (sp_ListaFrecuencia). */
    public List<MembresiaDTO> getListaFrecuencias() {
        if (listaFrecuencias == null) {
            try { listaFrecuencias = getFacade().ListaFrecuenciaF(); }
            catch (RuntimeException ex) { listaFrecuencias = Collections.emptyList(); }
        }
        return listaFrecuencias;
    }

    public String buscar() { return null; }

    public String limpiar() {
        filtro = null;
        listaPlanes = null;
        return VISTA_LISTA;
    }

    public String eliminar(String codigoM) {
        // Regla: no se elimina un plan que ya tiene suscripciones registradas.
        if (getFacade().Listasuscripciones_MembresiaXCodMembresiaF(codEmpSesion(), codigoM) != null) {
            mensaje(FacesMessage.SEVERITY_ERROR,
                    "El plan tiene suscripciones registradas; no se puede eliminar. Puede marcarlo como Inactivo.");
            return null;
        }
        MembresiaDTO eliminar = new MembresiaDTO();
        eliminar.setCodigoM(codigoM);
        boolean eliminado = getFacade().DeleteMembresiaF(eliminar);
        listaPlanes = null;
        if (eliminado) {
            mensaje(FacesMessage.SEVERITY_INFO, "Plan de membresía eliminado correctamente.");
        } else {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo eliminar el plan de membresía.");
        }
        return null;
    }

    // ---------------------------------------------------------
    // REGISTRO / ACTUALIZACIÓN DE PLANES
    // ---------------------------------------------------------

    /**
     * f:viewAction de Registro-plan-membresia.xhtml (solo en peticiones GET):
     * con ?codigo=XXXX carga el plan para editarlo, sin parámetro prepara uno nuevo.
     */
    public String prepararFormulario() {
        if (vacio(codigoParam)) {
            crearNuevo();
            return null;
        }
        MembresiaDTO encontrado = getFacade().ListaMembresiaXCodF(codigoParam.trim());
        codigoParam = null;
        if (encontrado == null) {
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            mensaje(FacesMessage.SEVERITY_ERROR, "No se encontró el plan de membresía seleccionado.");
            return VISTA_LISTA;
        }
        encontrado.setEstado(normalizarEstado(encontrado.getEstado()));
        plan = encontrado;
        modo = "actualizar";
        return null;
    }

    private void crearNuevo() {
        plan = new MembresiaDTO();
        plan.setCodigoM(getFacade().autogeneraCodMembresiaF());
        plan.setEstado("Activo");
        List<MembresiaDTO> frecuencias = getListaFrecuencias();
        if (!frecuencias.isEmpty()) plan.setIDFrecuencia(frecuencias.get(0).getIDFrecuencia());
        modo = "grabar";
    }

    public String guardar() {
        MembresiaDTO p = getPlan();

        if (vacio(p.getCodigoM())) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo generar el código del plan. Revise la conexión.");
            return null;
        }
        if (vacio(p.getNombreMembresia())) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Ingrese el nombre del plan.");
            return null;
        }
        p.setNombreMembresia(p.getNombreMembresia().trim());
        if (p.getNombreMembresia().length() > 50) {
            mensaje(FacesMessage.SEVERITY_ERROR, "El nombre del plan no puede superar los 50 caracteres.");
            return null;
        }
        if (p.getDuracionDias() < 0 || p.getDuracionSemanas() < 0
                || p.getDuracionMeses() < 0 || p.getDuracionAnios() < 0 || p.getDiasLimite() < 0) {
            mensaje(FacesMessage.SEVERITY_ERROR, "La duración y el límite de días no pueden ser negativos.");
            return null;
        }
        if (totalDias(p) <= 0) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Indique la duración del plan (días, semanas, meses o años).");
            return null;
        }
        if (p.getTarifa() <= 0) {
            mensaje(FacesMessage.SEVERITY_ERROR, "La tarifa debe ser mayor a cero.");
            return null;
        }
        if (p.getIDFrecuencia() <= 0) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Seleccione la frecuencia del plan.");
            return null;
        }
        if (vacio(p.getEstado())) p.setEstado("Activo");

        boolean edicion = isEdicion();
        boolean ok = edicion ? getFacade().UpdateMembresiaF(p) : getFacade().InsertMembresiaF(p);
        if (!ok) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar el plan. Revise los datos y la conexión.");
            return null;
        }

        listaPlanes = null;
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put(
                "mensajePlanJSF", edicion
                        ? "Plan de membresía actualizado correctamente."
                        : "Plan de membresía registrado correctamente.");
        plan = null;
        modo = "grabar";
        return VISTA_LISTA;
    }

    public String cancelar() {
        plan = null;
        modo = "grabar";
        return VISTA_LISTA;
    }

    // ---------------------------------------------------------
    // AYUDAS DE PRESENTACIÓN
    // ---------------------------------------------------------

    /** Texto de duración, p. ej. "1 mes 2 semanas". */
    public String duracionTexto(MembresiaDTO p) { return textoDuracion(p); }

    public static String textoDuracion(MembresiaDTO p) {
        if (p == null) return "";
        StringBuilder sb = new StringBuilder();
        agregar(sb, p.getDuracionAnios(), "año", "años");
        agregar(sb, p.getDuracionMeses(), "mes", "meses");
        agregar(sb, p.getDuracionSemanas(), "semana", "semanas");
        agregar(sb, p.getDuracionDias(), "día", "días");
        return sb.length() == 0 ? "Sin duración" : sb.toString();
    }

    /** Equivalente aproximado en días (año = 365, mes = 30, semana = 7). */
    public int totalDias(MembresiaDTO p) { return diasEquivalentes(p); }

    public static int diasEquivalentes(MembresiaDTO p) {
        if (p == null) return 0;
        return p.getDuracionAnios() * 365 + p.getDuracionMeses() * 30
                + p.getDuracionSemanas() * 7 + p.getDuracionDias();
    }

    public boolean esActivo(MembresiaDTO p) { return planActivo(p); }

    public static boolean planActivo(MembresiaDTO p) {
        return p != null && p.getEstado() != null
                && p.getEstado().trim().toUpperCase(Locale.ROOT).startsWith("ACTIV");
    }

    public boolean isEdicion() { return "actualizar".equals(modo); }
    public String getModo() { return modo; }

    public MembresiaDTO getPlan() {
        if (plan == null) crearNuevo();
        return plan;
    }
    public void setPlan(MembresiaDTO plan) { this.plan = plan; }

    public String getCodigoParam() { return codigoParam; }
    public void setCodigoParam(String codigoParam) { this.codigoParam = codigoParam; }

    public String getFiltro() { return filtro; }
    public void setFiltro(String filtro) { this.filtro = filtro; }

    // ---------------------------------------------------------
    // UTILIDADES
    // ---------------------------------------------------------

    /** CodEmp del usuario en sesión (VARCHAR(2) en BD). */
    static String codEmpSesion() {
        UsuarioDTO u = usuarioEnSesion();
        String cod = u != null ? u.getCodEmp() : null;
        return vacio(cod) ? COD_EMP_DEFECTO : cod.trim();
    }

    static UsuarioDTO usuarioEnSesion() {
        Object value = FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("usuarioAutenticado");
        return value instanceof UsuarioDTO ? (UsuarioDTO) value : null;
    }

    /** Estado es CHAR(10): llega con espacios y en mayúsculas o minúsculas. */
    private static String normalizarEstado(String estado) {
        if (estado == null) return "Activo";
        return estado.trim().toUpperCase(Locale.ROOT).startsWith("INACTIV") ? "Inactivo" : "Activo";
    }

    private static void agregar(StringBuilder sb, int valor, String singular, String plural) {
        if (valor <= 0) return;
        if (sb.length() > 0) sb.append(' ');
        sb.append(valor).append(' ').append(valor == 1 ? singular : plural);
    }
    private static boolean contiene(String valor, String texto) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(texto);
    }
    static boolean vacio(String s) { return s == null || s.trim().isEmpty(); }
    static void mensaje(FacesMessage.Severity severity, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, texto, null));
    }
}
