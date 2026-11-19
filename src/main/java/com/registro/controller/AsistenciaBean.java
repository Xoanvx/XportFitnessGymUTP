package com.registro.controller;

import com.registro.dto.AsistenciaDTO;
import com.registro.dto.ClienteDTO;
import com.registro.facade.AsistenciaFACADE;
import com.registro.facade.ClienteFACADE;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Registro de asistencia: el socio digita su codigo (Cliente_ID) o DNI. */
@Named("asistenciaBean")
@SessionScoped
public class AsistenciaBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String TURNO_DEFECTO = "01";

    private transient ClienteFACADE clienteFacade;
    private transient AsistenciaFACADE asistenciaFacade;

    private String codigo;
    private String nombreSocio;

    private ClienteFACADE clientes() {
        if (clienteFacade == null) clienteFacade = new ClienteFACADE();
        return clienteFacade;
    }

    private AsistenciaFACADE asistencias() {
        if (asistenciaFacade == null) asistenciaFacade = new AsistenciaFACADE();
        return asistenciaFacade;
    }

    /** Accion del boton "Ingresar": 4 validaciones secuenciales y registro. */
    public String registrar() {
        nombreSocio = null;
        String cod = codigo == null ? "" : codigo.trim();

        // 1) El socio debe existir (por Cliente_ID y, si no, por DNI/RUC)
        ClienteDTO cliente = cod.isEmpty() ? null : clientes().ListaClienteXCod(cod);
        if (cliente == null && !cod.isEmpty()) {
            cliente = clientes().ListaClienteXRucDni(cod);
        }
        if (cliente == null) {
            error("No existe el socio con este código");
            return limpiar();
        }
        String clienteId = cliente.getCliente_ID();

        // 2) Membresia registrada y vigente hoy
        String nroSuscripcion = asistencias().buscarSuscripcionVigenteF(MembresiaBean.codEmpSesion(), clienteId);
        if (nroSuscripcion == null) {
            error("No existe el socio como membresía registrada");
            return limpiar();
        }

        // 3) No repetir asistencia el mismo dia
        if (asistencias().existeAsistenciaHoyF(clienteId)) {
            error("Ya registró su asistencia hoy, no puede repetir");
            return limpiar();
        }

        // 4) Registrar: HoraIngreso = ahora, FechaIngreso = hoy (sin hora)
        LocalDateTime ahora = LocalDateTime.now();
        AsistenciaDTO a = new AsistenciaDTO();
        a.setNroSuscripcion_id(nroSuscripcion);
        a.setCliente_ID(clienteId);
        a.setFechaHora(Timestamp.valueOf(ahora));
        a.setFechaFin(LocalDate.now().atStartOfDay());   // el DAO la envia como @FechaFin = FechaIngreso
        a.setTurno(TURNO_DEFECTO);

        if (asistencias().InsertDetRegistroAsistenciasF(a)) {
            nombreSocio = cliente.getRazonSocial();
            info("Asistencia registrada correctamente");
        } else {
            error("No se pudo registrar la asistencia. Intente nuevamente.");
        }
        return limpiar();
    }

    /** Boton "Nuevo". */
    public String nuevo() {
        nombreSocio = null;
        return limpiar();
    }

    private String limpiar() {
        codigo = null;
        return null;   // permanece en la misma vista
    }

    private static void error(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }

    private static void info(String msg) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null));
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombreSocio() { return nombreSocio; }
}
