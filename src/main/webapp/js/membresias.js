/*
 * Suscripción de membresía (estructura de SisdataGimnasio/frm_RegistroMembresia).
 * Comportamiento solo de interfaz: selector de planes, fechas, descuento/total y atajos.
 * Registrar, buscar socio, comprobante e impresión se conectarán al controlador.
 */
document.addEventListener("DOMContentLoaded", function () {
    const $ = function (id) { return document.getElementById(id); };
    const form = $("formMembresia");
    const selector = $("selectorPlanes");

    function hoyISO() {
        const d = new Date();
        d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
        return d.toISOString().slice(0, 10);
    }

    function dinero(valor) {
        const n = parseFloat(valor);
        return isNaN(n) ? "0.00" : n.toFixed(2);
    }

    function irDashboard() {
        const enlace = document.querySelector('.sidebar a[href*="dashboard"]');
        if (enlace) window.location.href = enlace.href;
        else window.history.back();
    }

    function aviso(texto) { alert(texto); }

    // ---------- Totales ----------
    function recalcularTotal() {
        const subtotal = parseFloat($("subtotal").value) || 0;
        let descuento = $("aplicarDescuento").checked ? (parseFloat($("descuento").value) || 0) : 0;
        if (descuento < 0) descuento = 0;
        if (descuento > subtotal) descuento = subtotal;
        $("total").value = dinero(subtotal - descuento);
    }

    $("aplicarDescuento").addEventListener("change", function () {
        $("descuento").disabled = !this.checked;
        if (!this.checked) $("descuento").value = "0.00";
        else $("descuento").focus();
        recalcularTotal();
    });
    $("descuento").addEventListener("input", recalcularTotal);
    $("descuento").addEventListener("blur", function () { this.value = dinero(this.value); recalcularTotal(); });

    // ---------- Fechas ----------
    let mesesPlan = 0;
    function calcularVencimiento() {
        const inicio = $("fechaInicio").value;
        if (!inicio || !mesesPlan) return;
        const f = new Date(inicio + "T00:00:00");
        f.setMonth(f.getMonth() + mesesPlan);
        f.setMinutes(f.getMinutes() - f.getTimezoneOffset());
        $("fechaVencimiento").value = f.toISOString().slice(0, 10);
    }
    $("fechaInicio").addEventListener("change", calcularVencimiento);

    // ---------- Selector de planes [F5] ----------
    function mostrarPlanes() {
        selector.hidden = !selector.hidden;
        if (!selector.hidden) selector.scrollIntoView({ behavior: "smooth", block: "nearest" });
    }
    $("btnVerMembresias").addEventListener("click", mostrarPlanes);
    $("btnCerrarPlanes").addEventListener("click", function () { selector.hidden = true; });

    selector.querySelectorAll("tbody tr").forEach(function (fila) {
        fila.addEventListener("click", function () {
            selector.querySelectorAll("tbody tr").forEach(function (f) { f.classList.remove("seleccionado"); });
            fila.classList.add("seleccionado");

            const d = fila.dataset;
            $("codMembresia").value = d.codigo;
            $("nombreMembresia").value = d.nombre;
            $("diasLimite").value = d.limite;
            $("tarifa").value = dinero(d.tarifa);
            $("duracion").value = d.duracion;
            $("frecuencia").value = d.frecuencia;
            $("subtotal").value = dinero(d.tarifa);
            mesesPlan = parseInt(d.meses, 10) || 0;
            if (!$("fechaInicio").value) $("fechaInicio").value = hoyISO();
            calcularVencimiento();
            recalcularTotal();
            selector.hidden = true;
        });
    });

    // ---------- Botones ----------
    function nuevo() {
        form.reset();
        selector.hidden = true;
        mesesPlan = 0;
        $("descuento").disabled = true;
        $("fechaInicio").value = hoyISO();
        $("condicion").value = "ACTIVA";
        $("lblEstado").textContent = "Pendiente";
        $("lblEstado").className = "estado-badge estado-pendiente";
        recalcularTotal();
        $("codigo").focus();
    }

    function buscarSocio() {
        $("codigo").focus();
        aviso("La búsqueda de socios se conectará cuando implementemos la acción en el controlador.");
    }

    function registrar() {
        if (!$("codigo").value.trim()) { aviso("Ingrese o busque el socio [F6]."); $("codigo").focus(); return; }
        if (!$("codMembresia").value.trim()) { aviso("Seleccione la membresía con Ver membresías [F5]."); return; }
        aviso("El registro de la suscripción se implementará al conectar el formulario con la base de datos.");
    }

    $("btnNuevo").addEventListener("click", nuevo);
    $("btnRegistrar").addEventListener("click", registrar);
    $("btnBuscar").addEventListener("click", buscarSocio);
    $("btnNuevoSocio").addEventListener("click", function () {
        const enlace = document.querySelector('.sidebar a[href*="socios"]');
        if (enlace) window.location.href = enlace.href;
        else aviso("Registre el socio desde el módulo Socios.");
    });
    $("btnRenovar").addEventListener("click", function () {
        aviso("La renovación tomará la última membresía terminada del socio al conectar con la base de datos.");
    });
    $("btnEliminar").addEventListener("click", function () {
        if (confirm("¿Eliminar la suscripción " + $("nroRegistro").value + "?")) {
            aviso("La eliminación se implementará al conectar el formulario con la base de datos.");
        }
    });
    $("btnComprobante").addEventListener("click", function () {
        aviso("La generación del comprobante se implementará al conectar con la base de datos.");
    });
    $("btnImprimir").addEventListener("click", function () {
        aviso("La impresión de la suscripción se implementará al conectar con la base de datos.");
    });
    $("btnSalir").addEventListener("click", irDashboard);

    // ---------- Atajos de teclado (como en el formulario de escritorio) ----------
    document.addEventListener("keydown", function (event) {
        const acciones = {
            "F2": nuevo,
            "F3": registrar,
            "F4": function () { $("btnComprobante").click(); },
            "F5": mostrarPlanes,
            "F6": buscarSocio,
            "F7": function () { $("btnNuevoSocio").click(); },
            "Pause": function () { $("btnImprimir").click(); },
            "Escape": function () { if (!selector.hidden) selector.hidden = true; else irDashboard(); }
        };
        const accion = acciones[event.key];
        if (accion) {
            event.preventDefault();
            accion();
        }
    });

    // Estado inicial
    if (!$("fechaInicio").value) $("fechaInicio").value = hoyISO();
    recalcularTotal();
});
