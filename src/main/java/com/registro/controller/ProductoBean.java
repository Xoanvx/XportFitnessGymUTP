package com.registro.controller;

import com.registro.dto.CategoriaDTO;
import com.registro.dto.MarcasDTO;
import com.registro.dto.ProductosDTO;
import com.registro.dto.UsuarioDTO;
import com.registro.facade.ProductosFACADE;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**

 * Lo usan dos páginas:
 *  - RegistrarProducto.xhtml: solo el formulario para grabar y actualizar.
 *  - Productos.xhtml: el listado con los filtros de búsqueda, editar y eliminar.
 *  - Nuevo (F2): limpia el formulario y autogenera el código (sp_autogenera_codprod).
 *  - Grabar (F3): sp_Insert_Productos.
 *  - Actualizar (F4): sp_Update_Productos, o sp_Update_Productos2 (sin stock) si el producto ya tiene ventas.
 *  - Eliminar (botón de cada fila): no borra, deja el producto Inactivo (sp_Update_Estado_Producto).
 *  - Buscar (F5) según la condición marcada: nombre (sp_buscaProdxNomTotAct), marca
 *    (sp_buscaProdxMarcaActivo), código de barra o categoría (filtrando sp_Lista_ProductosGral_Activos).
 *  - Listar (F7): productos activos (sp_Lista_ProductosGral_Activos).
 */
@Named("productoBean")
@SessionScoped
public class ProductoBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String FILTRO_GENERAL = "GENERAL";
    private static final String FILTRO_NOMBRE = "NOMBRE";
    private static final String FILTRO_BARRA = "BARRA";
    private static final String FILTRO_CATEGORIA = "CATEGORIA";
    private static final String FILTRO_MARCA = "MARCA";

    private ProductosDTO producto;
    private boolean edicion;
    private boolean stockEditable = true;

    private List<ProductosDTO> lista = new ArrayList<>();
    private List<CategoriaDTO> listaCategorias = new ArrayList<>();
    private List<MarcasDTO> listaMarcas = new ArrayList<>();

    /** Condición marcada en la búsqueda: NOMBRE, BARRA, CATEGORIA o MARCA. */
    private String criterioBusqueda = FILTRO_NOMBRE;
    private String textoBusqueda;
    private int categoriaBusqueda;
    private int marcaBusqueda;
    /** Filtro aplicado al listado actual (GENERAL o uno de los criterios). */
    private String filtro = FILTRO_GENERAL;

    // Paginación (igual que ListaClientes): la lista se consulta una vez (Listar / Buscar / al entrar)
    // y se muestra por partes. Cambiar de página o de tamaño es un postback: no vuelve a consultar la BD.
    private static final int[] TAMANOS_PAGINA = {10, 25, 50, 100};
    private static final int PAGINAS_VISIBLES = 5;
    private int pagina = 1;
    private int tamPagina = 10;

    /** "editar" cuando se llega desde el botón Editar de Productos.xhtml (?modo=editar). */
    private String modo;

    @PostConstruct
    public void init() {
        cargarCombos();
        nuevo();
    }

    /**
     * Al abrir RegistrarProducto.xhtml: recarga categorías y marcas y, salvo que se venga a editar
     * un producto, prepara uno nuevo con su código (como al abrir frm_Productos).
     */
    public void prepararFormulario() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx.isPostback()) return;
        // Se lee de la petición: entrar desde el menú (sin ?modo=editar) siempre abre un producto nuevo.
        modo = ctx.getExternalContext().getRequestParameterMap().get("modo");
        cargarCombos();
        if (!"editar".equals(modo) || !edicion) nuevo();
    }

    /** Al abrir Productos.xhtml: consulta el listado con el último filtro usado. */
    public void prepararListado() {
        if (FacesContext.getCurrentInstance().isPostback()) return;
        cargarCombos();
        refrescarLista();
    }

    private void cargarCombos() {
        ProductosFACADE f = new ProductosFACADE();
        listaCategorias = f.ListaCategorias();
        listaMarcas = f.ListaMarcas();
    }

    // ------------------------------------------------------------------ acciones

    public String nuevo() {
        producto = new ProductosDTO();
        producto.setEstadoProducto("Activo");
        producto.setUnidadMedida("UND");
        producto.setTipoProd("V");
        ProductosDTO cod = new ProductosFACADE().AutogeneraProducto();
        producto.setProducto_ID(cod == null ? null : cod.getProducto_ID());
        edicion = false;
        stockEditable = true;
        return null;
    }

    public String grabar() {
        if (!validar()) return null;
        if (producto.getProducto_ID() == null || producto.getProducto_ID().isBlank()) {
            error("No se pudo generar el código del producto. Revise la conexión y pulse Nuevo.");
            return null;
        }
        prepararParaGuardar();
        if (new ProductosFACADE().Insert_ProductosD(producto)) {
            info("Se grabó correctamente el producto " + producto.getProducto_ID() + ".");
            nuevo();
        } else {
            error("No se pudo grabar el producto. Verifique que el código no exista y que los datos sean correctos.");
        }
        return null;
    }

    public String actualizar() {
        if (!edicion) {
            error("Para actualizar, elija el producto con el botón Editar en Ver Productos.");
            return null;
        }
        if (!validar()) return null;
        prepararParaGuardar();
        if (new ProductosFACADE().Update_Productos(producto, stockEditable)) {
            info("Se hicieron los cambios correctamente en el producto " + producto.getProducto_ID() + ".");
            nuevo();
            // Vuelve al listado, donde se eligió el producto.
            return "/Productos.xhtml?faces-redirect=true";
        } else {
            error("No se pudo actualizar el producto.");
        }
        return null;
    }

    /**
     * Botón Editar de Productos.xhtml: pasa la fila al formulario (doble clic / Enter en la grilla
     * de frm_Productos) y abre RegistrarProducto.xhtml en modo edición.
     */
    public String editar(ProductosDTO fila) {
        ProductosDTO p = new ProductosDTO();
        p.setProducto_ID(fila.getProducto_ID());
        p.setDescripcion(fila.getDescripcion());
        p.setNombreProducto(fila.getNombreProducto());
        p.setEstadoProducto(fila.getEstadoProducto());
        p.setCategoria_ID(idCategoria(fila.getCategoria()));
        p.setMarcaProducto_ID(idMarca(fila.getMarca()));
        p.setUnidadMedida(fila.getUnidadMedida());
        p.setStockFinal(fila.getStockFinal());
        p.setPrecioCosto(fila.getPrecioCosto());
        p.setPrecioVenta(fila.getPrecioVenta());
        p.setPrecioLimite(fila.getPrecioLimite());
        p.setMargenUtil(fila.getMargenUtil());
        p.setPeso(fila.getPeso());
        p.setImagen(fila.getImagen());
        p.setCodigoBarra(fila.getCodigoBarra());
        p.setTipoProd(fila.getTipoProd() == null || fila.getTipoProd().isBlank() ? "V" : fila.getTipoProd());
        producto = p;
        edicion = true;
        // Con ventas registradas el stock ya no se modifica desde aquí (se mueve con compras/ventas).
        stockEditable = !new ProductosFACADE().TieneVentas(p.getProducto_ID());
        return "/RegistrarProducto.xhtml?faces-redirect=true&modo=editar";
    }

    /** "Eliminar" de frm_Productos: cambia el estado a Inactivo. */
    public String eliminar(ProductosDTO fila) {
        if (new ProductosFACADE().Update_Estado_Producto(fila.getProducto_ID(), "Inactivo", codEmp())) {
            info("Se eliminó el producto " + fila.getProducto_ID() + " (quedó Inactivo).");
            if (edicion && fila.getProducto_ID().equals(producto.getProducto_ID())) nuevo();
            refrescarLista();
        } else {
            error("No se pudo eliminar el producto " + fila.getProducto_ID() + ".");
        }
        return null;
    }

    public String listar() {
        filtro = FILTRO_GENERAL;
        pagina = 1;
        refrescarLista();
        return null;
    }

    /** Cambia de página sin consultar la base de datos. */
    public String irAPagina(int numero) {
        pagina = numero;
        return null;
    }

    /** Buscar (F5) según la condición marcada: nombre, código de barra, categoría o marca. */
    public String buscar() {
        String criterio = criterioBusqueda == null ? FILTRO_NOMBRE : criterioBusqueda;
        switch (criterio) {
            case FILTRO_CATEGORIA:
                if (categoriaBusqueda <= 0) {
                    error("Seleccione la categoría a buscar.");
                    return null;
                }
                break;
            case FILTRO_MARCA:
                if (marcaBusqueda <= 0) {
                    error("Seleccione la marca a buscar.");
                    return null;
                }
                break;
            case FILTRO_BARRA:
            case FILTRO_NOMBRE:
                if (vacio(textoBusqueda)) {
                    error(FILTRO_BARRA.equals(criterio) ? "Ingrese el código de barra a buscar."
                                                        : "Ingrese el nombre del producto a buscar.");
                    return null;
                }
                break;
            default:
                criterio = FILTRO_NOMBRE;
        }
        filtro = criterio;
        pagina = 1;
        refrescarLista();
        if (lista.isEmpty()) info("No se encontraron productos con esa condición.");
        return null;
    }

    public String salir() {
        return "/Productos.xhtml?faces-redirect=true";
    }

    // ------------------------------------------------------------------ apoyo

    /** Repite el último listado (general, por nombre o por marca), como frm_Productos tras grabar. */
    private void refrescarLista() {
        ProductosFACADE f = new ProductosFACADE();
        String emp = codEmp();
        if (FILTRO_NOMBRE.equals(filtro)) {
            lista = f.BuscaProductosXNombre(emp, textoBusqueda.trim());
        } else if (FILTRO_MARCA.equals(filtro)) {
            lista = f.BuscaProductosXMarca(emp, marcaBusqueda);
        } else if (FILTRO_BARRA.equals(filtro)) {
            // No hay procedimiento de búsqueda por código de barra: se filtra el listado de activos.
            String buscado = textoBusqueda.trim().toUpperCase();
            lista = new ArrayList<>();
            for (ProductosDTO p : f.ListaProductosActivos(emp)) {
                if (p.getCodigoBarra() != null && p.getCodigoBarra().trim().toUpperCase().contains(buscado)) {
                    lista.add(p);
                }
            }
        } else if (FILTRO_CATEGORIA.equals(filtro)) {
            // El listado devuelve el nombre de la categoría: se compara con la categoría elegida.
            String nombre = nombreCategoria(categoriaBusqueda);
            lista = new ArrayList<>();
            for (ProductosDTO p : f.ListaProductosActivos(emp)) {
                if (p.getCategoria() != null && p.getCategoria().trim().equalsIgnoreCase(nombre)) {
                    lista.add(p);
                }
            }
        } else {
            lista = f.ListaProductosActivos(emp);
        }
    }

    private String nombreCategoria(int id) {
        for (CategoriaDTO c : listaCategorias) {
            if (c.getCategoria_ID() == id) return c.getNombre() == null ? "" : c.getNombre().trim();
        }
        return "";
    }

    private String nombreMarca(int id) {
        for (MarcasDTO m : listaMarcas) {
            if (m.getMarca_ID() == id) return m.getNombre() == null ? "" : m.getNombre().trim();
        }
        return "";
    }

    private boolean validar() {
        // En frm_Productos, al dar Enter en Descripción se copia al Nombre del producto.
        if (vacio(producto.getNombreProducto()) && !vacio(producto.getDescripcion())) {
            producto.setNombreProducto(producto.getDescripcion().trim());
        }
        if (vacio(producto.getDescripcion()) && !vacio(producto.getNombreProducto())) {
            producto.setDescripcion(producto.getNombreProducto().trim());
        }
        if (vacio(producto.getNombreProducto())) {
            error("Ingrese los datos completos: la descripción o el nombre del producto.");
            return false;
        }
        if (producto.getCategoria_ID() <= 0) {
            error("Seleccione la categoría del producto.");
            return false;
        }
        if (producto.getMarcaProducto_ID() <= 0) {
            error("Seleccione la marca del producto.");
            return false;
        }
        if (producto.getPrecioCosto() < 0 || producto.getPrecioVenta() < 0
                || producto.getPrecioLimite() < 0 || producto.getStockFinal() < 0 || producto.getPeso() < 0) {
            error("Los precios, el stock y el peso no pueden ser negativos.");
            return false;
        }
        return true;
    }

    /** Margen % = (PrecioVenta / PrecioCosto - 1) * 100, como CalculoUtilidad() de frm_Productos. */
    private void prepararParaGuardar() {
        double costo = producto.getPrecioCosto();
        double venta = producto.getPrecioVenta();
        double margen = costo > 0 ? Math.round((venta / costo - 1) * 100 * 100.0) / 100.0 : 0;
        producto.setMargenUtil(margen);
        producto.setNombreProducto(producto.getNombreProducto().trim());
        producto.setDescripcion(producto.getDescripcion().trim());
        producto.setCodEmp(codEmp());
        if (!"S".equals(producto.getTipoProd())) producto.setTipoProd("V");
    }

    private int idCategoria(String nombre) {
        for (CategoriaDTO c : listaCategorias) {
            if (c.getNombre() != null && c.getNombre().trim().equalsIgnoreCase(nombre == null ? "" : nombre.trim())) {
                return c.getCategoria_ID();
            }
        }
        return 0;
    }

    private int idMarca(String nombre) {
        for (MarcasDTO m : listaMarcas) {
            if (m.getNombre() != null && m.getNombre().trim().equalsIgnoreCase(nombre == null ? "" : nombre.trim())) {
                return m.getMarca_ID();
            }
        }
        return 0;
    }

    /** Empresa del usuario que inició sesión; "01" como en frm_Productos si no tiene. */
    private String codEmp() {
        Object u = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioAutenticado");
        if (u instanceof UsuarioDTO dto && dto.getCodEmp() != null && !dto.getCodEmp().isBlank()) {
            return dto.getCodEmp().trim();
        }
        return "01";
    }

    private static boolean vacio(String s) {
        return s == null || s.isBlank();
    }

    /** Los mensajes se conservan aunque la acción redirija a otra página. */
    private static void info(String texto) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, texto, null));
        ctx.getExternalContext().getFlash().setKeepMessages(true);
    }

    private static void error(String texto) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, texto, null));
        ctx.getExternalContext().getFlash().setKeepMessages(true);
    }

    // ------------------------------------------------------------------ getters / setters

    public ProductosDTO getProducto() { return producto; }
    public String getModo() { return modo; }
    public void setModo(String modo) { this.modo = modo; }
    public boolean isEdicion() { return edicion; }
    public boolean isStockEditable() { return stockEditable; }
    public List<ProductosDTO> getLista() { return lista; }
    public int getTotalItems() { return lista == null ? 0 : lista.size(); }

    /** Filas de la página actual. */
    public List<ProductosDTO> getListaPagina() {
        if (lista == null || lista.isEmpty()) return lista;
        int desde = (getPagina() - 1) * tamPagina;
        return lista.subList(desde, Math.min(desde + tamPagina, lista.size()));
    }

    /** Como en ListaClientes, la barra de paginación se muestra siempre que haya registros. */
    public boolean isConPaginacion() { return getTotalItems() > 0; }

    public int getTotalPaginas() { return Math.max(1, (getTotalItems() + tamPagina - 1) / tamPagina); }

    public int getTamPagina() { return tamPagina; }

    /** Solo 10, 25, 50 o 100; al cambiar el tamaño se vuelve a la primera página. */
    public void setTamPagina(int tam) {
        for (int permitido : TAMANOS_PAGINA) {
            if (permitido == tam) {
                if (tam != tamPagina) pagina = 1;
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

    public int getPagina() {
        if (pagina > getTotalPaginas()) pagina = getTotalPaginas();
        if (pagina < 1) pagina = 1;
        return pagina;
    }

    public int getDesde() { return getTotalItems() == 0 ? 0 : (getPagina() - 1) * tamPagina + 1; }
    public int getHasta() { return Math.min(getPagina() * tamPagina, getTotalItems()); }

    /** Hasta 5 números de página alrededor de la actual. */
    public List<Integer> getPaginasVisibles() {
        int total = getTotalPaginas();
        int inicio = Math.max(1, getPagina() - PAGINAS_VISIBLES / 2);
        int fin = Math.min(total, inicio + PAGINAS_VISIBLES - 1);
        inicio = Math.max(1, fin - PAGINAS_VISIBLES + 1);
        List<Integer> paginas = new ArrayList<>();
        for (int i = inicio; i <= fin; i++) paginas.add(i);
        return paginas;
    }
    public List<CategoriaDTO> getListaCategorias() { return listaCategorias; }
    public List<MarcasDTO> getListaMarcas() { return listaMarcas; }
    public String getTextoBusqueda() { return textoBusqueda; }
    public void setTextoBusqueda(String textoBusqueda) { this.textoBusqueda = textoBusqueda; }
    public int getMarcaBusqueda() { return marcaBusqueda; }
    public void setMarcaBusqueda(int marcaBusqueda) { this.marcaBusqueda = marcaBusqueda; }
    public int getCategoriaBusqueda() { return categoriaBusqueda; }
    public void setCategoriaBusqueda(int categoriaBusqueda) { this.categoriaBusqueda = categoriaBusqueda; }
    public String getCriterioBusqueda() { return criterioBusqueda; }
    public void setCriterioBusqueda(String criterioBusqueda) { this.criterioBusqueda = criterioBusqueda; }

    public String getDescripcionFiltro() {
        if (FILTRO_NOMBRE.equals(filtro)) return "Búsqueda por nombre: " + textoBusqueda;
        if (FILTRO_BARRA.equals(filtro)) return "Búsqueda por código de barra: " + textoBusqueda;
        if (FILTRO_CATEGORIA.equals(filtro)) return "Búsqueda por categoría: " + nombreCategoria(categoriaBusqueda);
        if (FILTRO_MARCA.equals(filtro)) return "Búsqueda por marca: " + nombreMarca(marcaBusqueda);
        return "Productos activos";
    }
}
