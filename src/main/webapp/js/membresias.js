/*
 * Suscripción de membresía (frm_RegistroMembresia).
 * La lógica (nuevo, planes, buscar socio, registrar, eliminar, totales y fechas) vive en
 * suscripcionMembresiaBean; este script solo conecta los atajos de teclado con los botones JSF.
 */
document.addEventListener("DOMContentLoaded", function () {
    const $ = function (id) { return document.getElementById(id); };

    function clic(id) {
        const el = $(id);
        if (el) el.click();
    }

    function irDashboard() {
        const enlace = document.querySelector('.sidebar a[href*="dashboard"]');
        if (enlace) window.location.href = enlace.href;
        else window.history.back();
    }

    function irSocios() {
        const enlace = document.querySelector('.sidebar a[href*="socios"]');
        if (enlace) window.location.href = enlace.href;
        else window.location.href = "socios.xhtml";
    }

    function aviso(texto) { alert(texto); }

    // ---------- Selector de planes: clic en la fila = clic en su enlace JSF ----------
    document.addEventListener("click", function (e) {
        const fila = e.target.closest(".tabla-planes tbody tr");
        if (!fila || e.target.closest("a")) return;
        const enlace = fila.querySelector("a");
        if (enlace) enlace.click();
    });

    // ---------- Botones sin conexión todavía (plantilla) ----------
    const nuevoSocio = $("btnNuevoSocio");
    if (nuevoSocio) nuevoSocio.addEventListener("click", irSocios);
    const salir = $("btnSalir");
    if (salir) salir.addEventListener("click", irDashboard);
    const renovar = $("btnRenovar");
    if (renovar) renovar.addEventListener("click", function () {
        aviso("La renovación tomará la última membresía terminada del socio al conectar con la base de datos.");
    });
    const comprobante = $("btnComprobante");
    if (comprobante) comprobante.addEventListener("click", function () {
        aviso("La generación del comprobante se implementará al conectar con la base de datos.");
    });
    const imprimir = $("btnImprimir");
    if (imprimir) imprimir.addEventListener("click", function () {
        aviso("La impresión de la suscripción se implementará al conectar con la base de datos.");
    });

    // Enter en el código del socio = Buscar [F6]
    document.addEventListener("keydown", function (e) {
        if (e.key === "Enter" && e.target && e.target.id === "codigo") {
            e.preventDefault();
            clic("btnBuscar");
        }
    });

    // ---------- Atajos de teclado (disparan los botones JSF reales) ----------
    document.addEventListener("keydown", function (event) {
        const acciones = {
            "F2": function () { clic("btnNuevo"); },
            "F3": function () { clic("btnRegistrar"); },
            "F4": function () { clic("btnComprobante"); },
            "F5": function () { clic("btnVerMembresias"); },
            "F6": function () { clic("btnBuscar"); },
            "F7": function () { clic("btnNuevoSocio"); },
            "Pause": function () { clic("btnImprimir"); },
            "Escape": function () { if ($("btnCerrarPlanes")) clic("btnCerrarPlanes"); else irDashboard(); }
        };
        const accion = acciones[event.key];
        if (accion) {
            event.preventDefault();
            accion();
        }
    });
});
