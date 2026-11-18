// Comportamiento de RegistrarAsistencia.xhtml
// PLANTILLA: muestra datos de ejemplo. Al enlazar con la BD, reemplazar registrarDemo()
// por la llamada al bean (AsistenciaFACADE).
(function () {
    var codigo = document.getElementById("raCodigo");
    var btnIngresar = document.getElementById("raIngresar");
    var btnNuevo = document.getElementById("raNuevo");
    var btnSalir = document.getElementById("raSalir");
    var fecha = document.getElementById("raFecha");
    var hora = document.getElementById("raHora");
    var bienvenida = document.getElementById("raBienvenida");
    var nombre = document.getElementById("raNombre");
    var avisos = document.getElementById("raAvisos");
    var periodo = document.getElementById("raPeriodo");
    var historial = document.getElementById("raHistorial");
    if (!codigo) return;

    var AVISO_VACIO = '<p class="ra-aviso-vacio">Registre un ingreso para ver los avisos del socio.</p>';

    function dos(n) { return (n < 10 ? "0" : "") + n; }
    function textoFecha(d) { return dos(d.getDate()) + "/" + dos(d.getMonth() + 1) + "/" + d.getFullYear(); }
    function textoHora(d) {
        var h = d.getHours(), ampm = h >= 12 ? "PM" : "AM";
        h = h % 12 || 12;
        return dos(h) + ":" + dos(d.getMinutes()) + ":" + dos(d.getSeconds()) + " " + ampm;
    }
    function escapar(t) {
        return String(t).replace(/[&<>"]/g, function (c) {
            return { "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c];
        });
    }

    // Reloj (equivale al timer1 del formulario de escritorio)
    function reloj() {
        var d = new Date();
        var dias = ["domingo", "lunes", "martes", "miércoles", "jueves", "viernes", "sábado"];
        fecha.textContent = dias[d.getDay()] + ", " + textoFecha(d);
        hora.textContent = textoHora(d);
    }
    reloj();
    setInterval(reloj, 1000);

    // Igual que LimpiarLectura: quita prefijos de QR ("SOCIO:00000012", "...?codigo=12") y espacios
    function limpiarLectura(t) {
        t = (t || "").trim();
        [":", "=", "/", "|", ";"].forEach(function (sep) {
            var k = t.lastIndexOf(sep);
            if (k >= 0 && k < t.length - 1) t = t.substring(k + 1);
        });
        t = t.replace(/\s+/g, "");
        if (/^\d+$/.test(t) && t.length < 8) t = ("00000000" + t).slice(-8);   // 12 -> 00000012
        return t;
    }

    function limpiarSocio() {
        bienvenida.hidden = true;
        nombre.textContent = "";
        avisos.innerHTML = AVISO_VACIO;
        periodo.innerHTML = '<tr class="ra-fila-vacia"><td colspan="8">Sin datos</td></tr>';
        historial.innerHTML = '<tr class="ra-fila-vacia"><td colspan="3">Sin asistencias</td></tr>';
    }

    function nuevo() {
        codigo.value = "";
        limpiarSocio();
        btnIngresar.disabled = false;
        codigo.focus();
    }

    // ---- Datos de ejemplo (plantilla) ----
    function registrarDemo(cod) {
        var hoy = new Date();
        var inicio = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
        var fin = new Date(hoy.getFullYear(), hoy.getMonth() + 1, 0);
        var asistencias = [];
        for (var d = 1, n = 1; d <= hoy.getDate(); d += 2, n++) {
            var f = new Date(hoy.getFullYear(), hoy.getMonth(), d, 7 + (d % 4), (d * 7) % 60, 0);
            asistencias.push({ dia: n, hora: textoHora(f), fecha: textoFecha(f) });
        }
        if (!asistencias.length || asistencias[asistencias.length - 1].fecha !== textoFecha(hoy)) {
            asistencias.push({ dia: asistencias.length + 1, hora: textoHora(hoy), fecha: textoFecha(hoy) });
        }
        return {
            exito: true,
            socio: "SOCIO DE EJEMPLO (" + cod + ")",
            permitidas: 20,
            usadas: asistencias.length,
            deuda: 50.00,
            periodo: { nro: "00000125", pedido: "00000340", periodo: "MENSUAL", duracion: "1 MES",
                       frecuencia: "INTERDIARIO", inicio: textoFecha(inicio), fin: textoFecha(fin), condicion: "VIGENTE" },
            asistencias: asistencias
        };
    }

    function mostrar(res) {
        nombre.textContent = res.socio;
        bienvenida.hidden = !res.exito;

        var html = "";
        if (res.permitidas > 0) {
            html += '<div class="ra-aviso ra-aviso-info"><i class="fa-solid fa-circle-info"></i>' +
                    'Máximo de asistencias: <strong>' + res.permitidas + '</strong> · ' +
                    'Total de asistencias: <strong>' + res.usadas + '</strong> · ' +
                    'Le quedan <strong>' + Math.max(res.permitidas - res.usadas, 0) + '</strong> asistencias</div>';
        } else {
            html += '<div class="ra-aviso ra-aviso-info"><i class="fa-solid fa-circle-info"></i>' +
                    'Total de asistencias: <strong>' + res.usadas + '</strong></div>';
        }
        if (res.deuda > 0) {
            html += '<div class="ra-aviso ra-aviso-deuda"><i class="fa-solid fa-triangle-exclamation"></i>' +
                    'Usted tiene una deuda de S/ <strong>' + res.deuda.toFixed(2) + '</strong> y está pendiente de pago</div>';
        }
        avisos.innerHTML = html;

        var p = res.periodo;
        periodo.innerHTML = "<tr><td>" + [p.nro, p.pedido, p.periodo, p.duracion, p.frecuencia, p.inicio, p.fin]
            .map(escapar).join("</td><td>") + '</td><td><span class="ra-condicion">' + escapar(p.condicion) + "</span></td></tr>";

        historial.innerHTML = res.asistencias.map(function (a) {
            return "<tr><td>" + a.dia + "</td><td>" + escapar(a.hora) + "</td><td>" + escapar(a.fecha) + "</td></tr>";
        }).join("");
    }

    // Normaliza la lectura (QR/DNI) y deja que el formulario JSF envie el codigo al bean (asistenciaBean.registrar)
    function ingresar(e) {
        var cod = limpiarLectura(codigo.value);
        if (!cod) { if (e) e.preventDefault(); codigo.value = ""; codigo.focus(); return; }
        codigo.value = cod;
    }

    btnIngresar.addEventListener("click", ingresar);    btnNuevo.addEventListener("click", nuevo);
    codigo.addEventListener("keydown", function (e) {
        if (e.key === "Enter") { e.preventDefault(); btnIngresar.click(); }
    });

    // Si el lector QR escribe con el cursor en otro lugar, el texto va a la casilla del código
    document.addEventListener("keydown", function (e) {
        if (e.key === "F2") { e.preventDefault(); nuevo(); return; }
        if (e.key === "Escape") { e.preventDefault(); if (btnSalir) window.location.href = btnSalir.href; return; }
        var t = e.target;
        if (t === codigo || e.ctrlKey || e.altKey || e.metaKey || e.key.length !== 1) return;
        if (t && (t.tagName === "INPUT" || t.tagName === "TEXTAREA" || t.tagName === "SELECT")) return;
        if (btnIngresar.disabled) nuevo();   // listo para el siguiente socio
        codigo.focus();
    });
}());
