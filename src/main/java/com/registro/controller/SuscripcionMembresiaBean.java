package com.registro.controller;

import com.registro.dto.MembresiaDTO;
import com.registro.dto.UsuarioDTO;
import com.registro.facade.MembresiaFACADE;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.inject.Named;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import static com.registro.controller.MembresiaBean.codEmpSesion;
import static com.registro.controller.MembresiaBean.mensaje;
import static com.registro.controller.MembresiaBean.usuarioEnSesion;
import static com.registro.controller.MembresiaBean.vacio;

/**
 * Controlador JSF para el mantenimiento de suscripciones de membresía:
 * generar número (sp_generasuscripciones_Membresia), grabar/actualizar
 * (sp_InsertSuscripciones_Membresia) y eliminar (sp_Eliminasuscripciones_Membresia).
 */
@Named("suscripcionMembresiaBean")
@SessionScoped
public class SuscripcionMembresiaBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient MembresiaFACADE facade = new MembresiaFACADE();

    private MembresiaFACADE getFacade() {
        if (facade == null) facade = new MembresiaFACADE();
        return facade;
    }

    private MembresiaDTO suscripcion;
    private String modo = "grabar";

    // ---------------------------------------------------------
    // NUEVA / EDITAR
    // ---------------------------------------------------------

    /** Prepara una suscripción nueva con el siguiente número correlativo. */
    public String nuevo() {
        suscripcion = new MembresiaDTO();
        suscripcion.setCodEmp(codEmpSesion());
        suscripcion.setNroSuscripcion_id(generarNumero());
        suscripcion.setFechaInicio(Date.valueOf(LocalDate.now()));
        suscripcion.setEstadoSusc("Activo");
        suscripcion.setCondicionSusc("ACTIVA");
        modo = "grabar";
        return null;
    }

    /** Siguiente NroSuscripcion_id de la empresa, p. ej. "00000012". */
    public String generarNumero() {
        MembresiaDTO generado = getFacade().generasuscripciones_MembresiaF(codEmpSesion());
        return generado != null ? generado.getNroSuscripcion_id() : null;
    }

    /** Carga una suscripción existente para modificarla. */
    public String editar(String nroSuscripcion) {
        MembresiaDTO encontrada = getFacade()
                .Listasuscripciones_MembresiaXRegistroF(codEmpSesion(), nroSuscripcion);
        if (encontrada == null) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se encontró la suscripción " + nroSuscripcion + ".");
            return null;
        }
        encontrada.setCodEmp(codEmpSesion());
        suscripcion = encontrada;
        modo = "actualizar";
        return null;
    }

    /** Al elegir el plan: calcula fecha fin, subtotal, total y días de clase. */
    public void aplicarPlan() {
        MembresiaDTO s = getSuscripcion();
        if (vacio(s.getCodigoM())) return;
        MembresiaDTO plan = getFacade().ListaMembresiaXCodF(s.getCodigoM());
        if (plan == null) return;

        LocalDate inicio = s.getFechaInicio() != null ? s.getFechaInicio().toLocalDate() : LocalDate.now();
        LocalDate fin = inicio.plusYears(plan.getDuracionAnios())
                .plusMonths(plan.getDuracionMeses())
                .plusWeeks(plan.getDuracionSemanas())
                .plusDays(plan.getDuracionDias());
        s.setFechaInicio(Date.valueOf(inicio));
        s.setFechaFin(Date.valueOf(fin));
        s.setSubTotal(plan.getTarifa());
        s.setTotalSusc(Math.max(0, plan.getTarifa() - s.getDescuento()));
        // DiasClases: límite de ingresos del plan; si es 0 (sin límite), los días de vigencia.
        int diasVigencia = (int) (fin.toEpochDay() - inicio.toEpochDay());
        s.setDias(plan.getDiasLimite() > 0 ? plan.getDiasLimite() : diasVigencia);
    }

    // ---------------------------------------------------------
    // GRABAR / ELIMINAR
    // ---------------------------------------------------------

    /** Inserta o actualiza la suscripción (el SP decide por NroSuscripcion_id). */
    public String grabar() {
        MembresiaDTO s = getSuscripcion();

        if (vacio(s.getcliente_id())) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Seleccione el socio de la suscripción.");
            return null;
        }
        if (vacio(s.getCodigoM())) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Seleccione el plan de membresía.");
            return null;
        }
        if (s.getFechaFin() == null) aplicarPlan();
        if (s.getFechaInicio() == null || s.getFechaFin() == null || s.getFechaFin().before(s.getFechaInicio())) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Las fechas de inicio y fin no son válidas.");
            return null;
        }
        if (s.getDescuento() < 0 || s.getDescuento() > s.getSubTotal()) {
            mensaje(FacesMessage.SEVERITY_ERROR, "El descuento no puede ser negativo ni mayor al subtotal.");
            return null;
        }
        s.setTotalSusc(s.getSubTotal() - s.getDescuento());

        if (vacio(s.getNroSuscripcion_id())) s.setNroSuscripcion_id(generarNumero());
        if (vacio(s.getNroSuscripcion_id())) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo generar el número de suscripción.");
            return null;
        }
        s.setCodEmp(codEmpSesion());
        UsuarioDTO vendedor = usuarioEnSesion();
        if (vendedor != null && s.getPersonal_ID() <= 0) s.setPersonal_ID(vendedor.getPersonal_ID());
        if (vacio(s.getEstadoSusc())) s.setEstadoSusc("Activo");
        if (vacio(s.getCondicionSusc())) s.setCondicionSusc("ACTIVA");

        if (!getFacade().InsertSuscripciones_MembresiaF(s)) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar la suscripción. Revise los datos y la conexión.");
            return null;
        }
        mensaje(FacesMessage.SEVERITY_INFO, isEdicion()
                ? "Suscripción " + s.getNroSuscripcion_id() + " actualizada correctamente."
                : "Suscripción " + s.getNroSuscripcion_id() + " registrada correctamente.");
        nuevo();
        return null;
    }

    /** Elimina la suscripción y su detalle de clases. */
    public String eliminar(String nroSuscripcion) {
        MembresiaDTO eliminar = new MembresiaDTO();
        eliminar.setCodEmp(codEmpSesion());
        eliminar.setNroSuscripcion_id(nroSuscripcion);
        if (getFacade().Eliminasuscripciones_MembresiaF(eliminar)) {
            mensaje(FacesMessage.SEVERITY_INFO, "Suscripción " + nroSuscripcion + " eliminada correctamente.");
            if (suscripcion != null && nroSuscripcion.equals(suscripcion.getNroSuscripcion_id())) nuevo();
        } else {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo eliminar la suscripción " + nroSuscripcion + ".");
        }
        return null;
    }

    public boolean isEdicion() { return "actualizar".equals(modo); }
    public String getModo() { return modo; }

    public MembresiaDTO getSuscripcion() {
        if (suscripcion == null) nuevo();
        return suscripcion;
    }
    public void setSuscripcion(MembresiaDTO suscripcion) { this.suscripcion = suscripcion; }
}
