package com.registro.controller;

import com.registro.dto.ClienteDTO;
import com.registro.dto.MembresiaDTO;
import com.registro.dto.UsuarioDTO;
import com.registro.facade.ClienteFACADE;
import com.registro.facade.MembresiaFACADE;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.inject.Named;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

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

    private static final String ESTADO_INICIAL = "Pendiente";
    private static final String CONDICION_INICIAL = "ACTIVA";
    private static final int MAX_FILAS_CRONOGRAMA = 60;

    private transient MembresiaFACADE facade = new MembresiaFACADE();
    private transient ClienteFACADE clienteFacade = new ClienteFACADE();

    private MembresiaFACADE getFacade() {
        if (facade == null) facade = new MembresiaFACADE();
        return facade;
    }

    private ClienteFACADE getClienteFacade() {
        if (clienteFacade == null) clienteFacade = new ClienteFACADE();
        return clienteFacade;
    }

    private MembresiaDTO suscripcion;
    private String modo = "grabar";

    // Planes [F5]
    private List<MembresiaDTO> planes = new ArrayList<>();
    private boolean mostrarPlanes;
    private String duracionTexto = "";

    // Socio [F6]
    private String codigoSocio;
    private String socioNombre;
    private String socioDireccion;
    private String socioTelefono;

    // Descuento
    private boolean aplicarDescuento;

    // ---------------------------------------------------------
    // PASO 1 - NUEVO [F2]
    // ---------------------------------------------------------

    /** Limpia el formulario y prepara una suscripción nueva con el siguiente número correlativo. */
    public String nuevo() {
        suscripcion = new MembresiaDTO();
        suscripcion.setCodEmp(codEmpSesion());
        suscripcion.setNroSuscripcion_id(generarNumero());
        suscripcion.setFechaInicio(Date.valueOf(LocalDate.now()));
        suscripcion.setEstadoSusc(ESTADO_INICIAL);
        suscripcion.setCondicionSusc(CONDICION_INICIAL);
        suscripcion.setObservacionSusc("");
        modo = "grabar";
        mostrarPlanes = false;
        duracionTexto = "";
        aplicarDescuento = false;
        limpiarSocio();
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
        aplicarDescuento = encontrada.getDescuento() > 0;
        mostrarPlanes = false;
        limpiarSocio();
        if (!vacio(encontrada.getcliente_id())) {
            ClienteDTO c = getClienteFacade().ListaClienteXCod(encontrada.getcliente_id());
            if (c != null) llenarSocio(c);
        }
        return null;
    }

    // ---------------------------------------------------------
    // PASO 2 - VER MEMBRESÍAS [F5]
    // ---------------------------------------------------------

    /** Muestra u oculta el selector con los planes reales de la BD. */
    public String verMembresias() {
        mostrarPlanes = !mostrarPlanes;
        if (mostrarPlanes) {
            ArrayList<MembresiaDTO> lista = getFacade().ListaMembresiaF();
            planes = lista != null ? lista : new ArrayList<>();
            if (planes.isEmpty()) {
                mensaje(FacesMessage.SEVERITY_WARN, "No hay planes de membresía registrados.");
            }
        }
        return null;
    }

    public String cerrarPlanes() {
        mostrarPlanes = false;
        return null;
    }

    /** Clic en un plan del selector. */
    public String seleccionarPlan(String codigoM) {
        MembresiaDTO s = getSuscripcion();
        s.setCodigoM(codigoM);
        aplicarPlan();
        if (!vacio(s.getCodigoM())) mostrarPlanes = false;
        return null;
    }

    /** Con el plan elegido: calcula fecha fin, tarifa, subtotal, total y días de clase. */
    public void aplicarPlan() {
        MembresiaDTO s = getSuscripcion();
        if (vacio(s.getCodigoM())) return;
        MembresiaDTO plan = getFacade().ListaMembresiaXCodF(s.getCodigoM());
        if (plan == null) {
            s.setCodigoM(null);
            mensaje(FacesMessage.SEVERITY_ERROR, "No se encontró el plan de membresía seleccionado.");
            return;
        }
        if (plan.getEstado() != null && plan.getEstado().trim().toUpperCase().startsWith("INACTIV")) {
            s.setCodigoM(null);
            mensaje(FacesMessage.SEVERITY_ERROR, "El plan de membresía seleccionado está inactivo.");
            return;
        }

        LocalDate inicio = s.getFechaInicio() != null ? s.getFechaInicio().toLocalDate() : LocalDate.now();
        LocalDate fin = inicio.plusYears(plan.getDuracionAnios())
                .plusMonths(plan.getDuracionMeses())
                .plusWeeks(plan.getDuracionSemanas())
                .plusDays(plan.getDuracionDias());
        s.setFechaInicio(Date.valueOf(inicio));
        s.setFechaFin(Date.valueOf(fin));

        s.setNombreMembresia(plan.getNombreMembresia());
        s.setFrecuencia(plan.getFrecuencia());
        s.setDiasLimite(plan.getDiasLimite());
        s.setTarifa(plan.getTarifa());
        s.setSubTotal(plan.getTarifa());
        recalcularTotal();

        // DiasClases: límite de ingresos del plan; si es 0 (sin límite), los días de vigencia.
        int diasVigencia = (int) (fin.toEpochDay() - inicio.toEpochDay());
        s.setDias(plan.getDiasLimite() > 0 ? plan.getDiasLimite() : diasVigencia);

        duracionTexto = textoDuracion(plan);
        if (!isEdicion()) {
            s.setEstadoSusc(ESTADO_INICIAL);
            s.setCondicionSusc(CONDICION_INICIAL);
        }
    }

    /** Listener del cambio de la fecha de inicio: recalcula el vencimiento si ya hay plan. */
    public void alCambiarFechaInicio() {
        if (!vacio(getSuscripcion().getCodigoM())) aplicarPlan();
    }

    /** Listener del checkbox "Aplicar descuento". */
    public void alCambiarAplicarDescuento() {
        if (!aplicarDescuento) getSuscripcion().setDescuento(0);
        recalcularTotal();
    }

    /** Listener del monto del descuento. */
    public void alCambiarDescuento() {
        recalcularTotal();
    }

    private void recalcularTotal() {
        MembresiaDTO s = getSuscripcion();
        double descuento = aplicarDescuento ? s.getDescuento() : 0;
        if (descuento < 0) descuento = 0;
        if (descuento > s.getSubTotal()) descuento = s.getSubTotal();
        s.setDescuento(descuento);
        s.setTotalSusc(s.getSubTotal() - descuento);
    }

    private static String textoDuracion(MembresiaDTO p) {
        StringBuilder sb = new StringBuilder();
        agregar(sb, p.getDuracionAnios(), "año", "años");
        agregar(sb, p.getDuracionMeses(), "mes", "meses");
        agregar(sb, p.getDuracionSemanas(), "semana", "semanas");
        agregar(sb, p.getDuracionDias(), "día", "días");
        return sb.length() == 0 ? "0 días" : sb.toString();
    }

    private static void agregar(StringBuilder sb, int valor, String singular, String plural) {
        if (valor <= 0) return;
        if (sb.length() > 0) sb.append(", ");
        sb.append(valor).append(' ').append(valor == 1 ? singular : plural);
    }

    // ---------------------------------------------------------
    // PASO 3 - BUSCAR SOCIO [F6]
    // ---------------------------------------------------------

    /** Busca por código de cliente, luego por DNI/RUC y, si no es numérico, por nombre. */
    public String buscarSocio() {
        String cod = codigoSocio == null ? "" : codigoSocio.trim();
        if (cod.isEmpty()) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Ingrese el código o DNI del socio.");
            return null;
        }

        ClienteDTO c = getClienteFacade().ListaClienteXCod(cod);
        if (c == null) c = getClienteFacade().ListaClienteXRucDni(cod);
        if (c == null && cod.matches("\\d{1,7}")) {
            c = getClienteFacade().ListaClienteXCod(String.format("%08d", Long.parseLong(cod)));
        }
        if (c == null && !cod.matches("\\d+")) {
            List<ClienteDTO> porNombre = getClienteFacade().ListaClientesXNombre(cod);
            if (porNombre != null && porNombre.size() == 1) {
                c = porNombre.get(0);
            } else if (porNombre != null && porNombre.size() > 1) {
                mensaje(FacesMessage.SEVERITY_WARN, "Se encontraron " + porNombre.size()
                        + " socios con ese nombre. Digite el código o DNI.");
                return null;
            }
        }

        if (c == null) {
            limpiarSocio();
            codigoSocio = cod;
            mensaje(FacesMessage.SEVERITY_ERROR, "No existe el socio con este código");
            return null;
        }
        llenarSocio(c);
        return null;
    }

    private void llenarSocio(ClienteDTO c) {
        String id = c.getCliente_ID() == null ? null : c.getCliente_ID().trim();
        getSuscripcion().setcliente_id(id);
        codigoSocio = id;
        socioNombre = c.getRazonSocial();
        socioDireccion = c.getDireccion();
        socioTelefono = c.getTelefono();
    }

    private void limpiarSocio() {
        if (suscripcion != null) suscripcion.setcliente_id(null);
        codigoSocio = "";
        socioNombre = "";
        socioDireccion = "";
        socioTelefono = "";
    }

    // ---------------------------------------------------------
    // PASO 4 - REGISTRAR [F3] / ELIMINAR
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
        if (s.getFechaInicio() == null || s.getFechaFin() == null) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Las fechas de inicio y fin no son válidas.");
            return null;
        }

        // Validación 1: fechas
        if (s.getFechaFin().before(s.getFechaInicio())) {
            mensaje(FacesMessage.SEVERITY_ERROR, "La fecha de vencimiento no puede ser menor que la de inicio");
            return null;
        }

        if (vacio(s.getNroSuscripcion_id())) s.setNroSuscripcion_id(generarNumero());
        if (vacio(s.getNroSuscripcion_id())) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo generar el número de suscripción.");
            return null;
        }

        // Validación 2: registro duplicado (solo al crear)
        if (!isEdicion()
                && getFacade().Listasuscripciones_MembresiaXRegistroF(codEmpSesion(), s.getNroSuscripcion_id()) != null) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No puede registrar el pago porque ya se registró esta membresía");
            return null;
        }

        if (s.getDescuento() < 0 || s.getDescuento() > s.getSubTotal()) {
            mensaje(FacesMessage.SEVERITY_ERROR, "El descuento no puede ser negativo ni mayor al subtotal.");
            return null;
        }
        s.setTotalSusc(s.getSubTotal() - s.getDescuento());

        s.setCodEmp(codEmpSesion());
        UsuarioDTO vendedor = usuarioEnSesion();
        if (vendedor != null && s.getPersonal_ID() <= 0) s.setPersonal_ID(vendedor.getPersonal_ID());
        if (vacio(s.getEstadoSusc())) s.setEstadoSusc(ESTADO_INICIAL);
        if (vacio(s.getCondicionSusc())) s.setCondicionSusc(CONDICION_INICIAL);

        if (!getFacade().InsertSuscripciones_MembresiaF(s)) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar la suscripción. Revise los datos y la conexión.");
            return null;
        }
        String nro = s.getNroSuscripcion_id();
        boolean edicion = isEdicion();
        nuevo();
        mensaje(FacesMessage.SEVERITY_INFO, edicion
                ? "Suscripción " + nro + " actualizada correctamente."
                : "Suscripción " + nro + " registrada correctamente.");
        return null;
    }

    /** Elimina la suscripción y su detalle de clases. */
    public String eliminar(String nroSuscripcion) {
        if (vacio(nroSuscripcion)
                || getFacade().Listasuscripciones_MembresiaXRegistroF(codEmpSesion(), nroSuscripcion) == null) {
            mensaje(FacesMessage.SEVERITY_WARN, "La suscripción " + (nroSuscripcion == null ? "" : nroSuscripcion)
                    + " aún no está registrada; no hay nada que eliminar.");
            return null;
        }
        MembresiaDTO eliminar = new MembresiaDTO();
        eliminar.setCodEmp(codEmpSesion());
        eliminar.setNroSuscripcion_id(nroSuscripcion);
        if (getFacade().Eliminasuscripciones_MembresiaF(eliminar)) {
            nuevo();
            mensaje(FacesMessage.SEVERITY_INFO, "Suscripción " + nroSuscripcion + " eliminada correctamente.");
        } else {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo eliminar la suscripción " + nroSuscripcion + ".");
        }
        return null;
    }

    // ---------------------------------------------------------
    // Fechas como texto ISO (yyyy-MM-dd) para <input type="date">
    // ---------------------------------------------------------

    public String getFechaInicioTxt() {
        Date d = getSuscripcion().getFechaInicio();
        return d == null ? "" : d.toLocalDate().toString();
    }

    public void setFechaInicioTxt(String txt) {
        getSuscripcion().setFechaInicio(parsear(txt));
    }

    public String getFechaFinTxt() {
        Date d = getSuscripcion().getFechaFin();
        return d == null ? "" : d.toLocalDate().toString();
    }

    public void setFechaFinTxt(String txt) {
        getSuscripcion().setFechaFin(parsear(txt));
    }

    private static Date parsear(String txt) {
        if (txt == null || txt.trim().isEmpty()) return null;
        try {
            return Date.valueOf(LocalDate.parse(txt.trim()));
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    // ---------------------------------------------------------
    // Getters / setters
    // ---------------------------------------------------------

    /** Filas del cronograma (clases de la suscripción). */
    public List<Integer> getCronograma() {
        List<Integer> filas = new ArrayList<>();
        int total = Math.min(getSuscripcion().getDias(), MAX_FILAS_CRONOGRAMA);
        for (int i = 1; i <= total; i++) filas.add(i);
        return filas;
    }

    public boolean isEdicion() { return "actualizar".equals(modo); }
    public String getModo() { return modo; }

    public MembresiaDTO getSuscripcion() {
        if (suscripcion == null) nuevo();
        return suscripcion;
    }
    public void setSuscripcion(MembresiaDTO suscripcion) { this.suscripcion = suscripcion; }

    public List<MembresiaDTO> getPlanes() { return planes; }
    public boolean isMostrarPlanes() { return mostrarPlanes; }
    public String getDuracionTexto() { return duracionTexto; }

    public String getCodigoSocio() { return codigoSocio; }
    public void setCodigoSocio(String codigoSocio) { this.codigoSocio = codigoSocio; }
    public String getSocioNombre() { return socioNombre; }
    public String getSocioDireccion() { return socioDireccion; }
    public String getSocioTelefono() { return socioTelefono; }

    public boolean isAplicarDescuento() { return aplicarDescuento; }
    public void setAplicarDescuento(boolean aplicarDescuento) { this.aplicarDescuento = aplicarDescuento; }

    public String getClaseEstado() {
        String e = getSuscripcion().getEstadoSusc();
        return e != null && e.trim().equalsIgnoreCase(ESTADO_INICIAL) ? "estado-pendiente" : "estado-activa";
    }
}
