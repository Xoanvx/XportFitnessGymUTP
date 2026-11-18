package com.registro.controller;

import com.registro.dto.ClienteDTO;
import com.registro.facade.ClienteFACADE;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Acciones JSF para listar, registrar, editar y eliminar socios/clientes. */
@Named("clienteBean")
@SessionScoped
public class ClienteBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient ClienteFACADE facade = new ClienteFACADE();

    private ClienteFACADE getFacade() {
        if (facade == null) facade = new ClienteFACADE();
        return facade;
    }
    private ClienteDTO socio;
    private List<ClienteDTO> listaClientes;
    private String modo = "grabar";

    // Paginación del listado (la lista se consulta una vez y se muestra por partes)
    private static final int[] TAMANOS_PAGINA = {10, 25, 50, 100};
    private static final int PAGINAS_VISIBLES = 5;
    private int pagina = 1;
    private int tamPagina = 10;

    @PostConstruct
    public void init() {
        prepararNuevo(false);
    }

    public List<ClienteDTO> getListaClientes() {
        if (listaClientes == null) {
            try {
                listaClientes = getFacade().ObtenerListadoCliente();
            } catch (RuntimeException ex) {
                listaClientes = Collections.emptyList();
                mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo cargar la lista de socios.");
            }
        }
        return listaClientes;
    }

    /** Solo los socios de la página actual. */
    public List<ClienteDTO> getClientesPagina() {
        List<ClienteDTO> todos = getListaClientes();
        if (todos.isEmpty()) return todos;
        int desde = (getPagina() - 1) * tamPagina;
        int hasta = Math.min(desde + tamPagina, todos.size());
        return todos.subList(desde, hasta);
    }

    public int getTotalRegistros() { return getListaClientes().size(); }

    public int getTotalPaginas() {
        return Math.max(1, (getTotalRegistros() + tamPagina - 1) / tamPagina);
    }

    /** Página actual, siempre dentro del rango válido. */
    public int getPagina() {
        if (pagina > getTotalPaginas()) pagina = getTotalPaginas();
        if (pagina < 1) pagina = 1;
        return pagina;
    }

    public void setPagina(int pagina) { this.pagina = pagina; }

    public int getTamPagina() { return tamPagina; }

    public void setTamPagina(int tam) {
        for (int permitido : TAMANOS_PAGINA) {
            if (permitido == tam) {
                if (tam != tamPagina) pagina = 1;   // al cambiar el tamaño se vuelve al inicio
                tamPagina = tam;
                return;
            }
        }
    }

    public List<Integer> getTamanosPagina() {
        List<Integer> tamanos = new ArrayList<>();
        for (int t : TAMANOS_PAGINA) tamanos.add(t);
        return tamanos;
    }

    /** Número del primer registro mostrado (1, 11, 21...). */
    public int getDesde() {
        return getTotalRegistros() == 0 ? 0 : (getPagina() - 1) * tamPagina + 1;
    }

    public int getHasta() { return Math.min(getPagina() * tamPagina, getTotalRegistros()); }

    /** Números de página a mostrar alrededor de la actual (máximo 5). */
    public List<Integer> getPaginasVisibles() {
        int total = getTotalPaginas();
        int inicio = Math.max(1, getPagina() - PAGINAS_VISIBLES / 2);
        int fin = Math.min(total, inicio + PAGINAS_VISIBLES - 1);
        inicio = Math.max(1, fin - PAGINAS_VISIBLES + 1);
        List<Integer> paginas = new ArrayList<>();
        for (int i = inicio; i <= fin; i++) paginas.add(i);
        return paginas;
    }

    public String nuevo() {
        prepararNuevo(true);
        return "/socios.xhtml?faces-redirect=true";
    }

    private void prepararNuevo(boolean resetList) {
        try {
            socio = getFacade().autogenera_CodCliente();
        } catch (RuntimeException ex) {
            socio = null;
        }
        if (socio == null) socio = new ClienteDTO();
        if (socio.getEstado() == null || socio.getEstado().isBlank()) socio.setEstado("Activo");
        modo = "grabar";
        if (resetList) listaClientes = null;
    }

    public String editar(String id) {
        socio = getFacade().buscarCliente(id);
        if (socio == null) {
            prepararNuevo(false);
            mensaje(FacesMessage.SEVERITY_ERROR, "No se encontró el socio seleccionado.");
            return "/ListaClientes.xhtml?faces-redirect=true";
        }
        modo = "actualizar";
        return "/socios.xhtml?faces-redirect=true";
    }

    public String guardar() {
        if (socio == null || socio.getRazonSocial() == null || socio.getRazonSocial().isBlank()) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Ingrese el nombre o razón social del socio.");
            return null;
        }
        try {
            boolean ok;
            if ("actualizar".equals(modo)) {
                ok = getFacade().actualizarCliente(socio);
            } else {
                ok = getFacade().registrarCliente(socio);
            }
            if (!ok) {
                mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo guardar el socio. Verifique los datos y la conexión.");
                return null;
            }
            listaClientes = null;
            FacesContext.getCurrentInstance().getExternalContext().getFlash()
                    .put("mensajeSocios", "actualizar".equals(modo)
                            ? "Socio actualizado correctamente." : "Socio registrado correctamente.");
            prepararNuevo(false);
            return "/ListaClientes.xhtml?faces-redirect=true";
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Ocurrió un error al guardar el socio.");
            return null;
        }
    }

    public String eliminar(String id) {
        ClienteDTO cliente = new ClienteDTO();
        cliente.setCliente_ID(id);
        try {
            if (getFacade().EliminarCliente(cliente)) {
                listaClientes = null;
                mensaje(FacesMessage.SEVERITY_INFO, "Socio eliminado correctamente.");
            } else {
                mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo eliminar el socio.");
            }
        } catch (RuntimeException ex) {
            mensaje(FacesMessage.SEVERITY_ERROR, "Ocurrió un error al eliminar el socio.");
        }
        return null;
    }

    public String cancelar() {
        prepararNuevo(false);
        return "/ListaClientes.xhtml?faces-redirect=true";
    }

    public String getModo() { return modo; }
    public void setModo(String modo) { this.modo = modo; }
    public ClienteDTO getSocio() {
        if (socio == null) socio = new ClienteDTO();
        return socio;
    }
    public void setSocio(ClienteDTO socio) { this.socio = socio; }

    private void mensaje(FacesMessage.Severity severity, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, texto, null));
    }
}
