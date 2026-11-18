/*
 * Paginación común de los listados (mismo diseño que ListaClientes).
 *
 * Uso: poner data-paginar="<nombre de los registros>" en el contenedor de la
 * tabla (o en la tabla), por ejemplo:
 *     <div class="tabla-scroll" data-paginar="categorías">
 * Debajo se dibuja la barra "Mostrando 1 a 10 de N ..." con los botones y el
 * selector Mostrar 10 / 25 / 50 / 100.
 *
 * Respeta los filtros de cada página: las filas que el filtro oculta
 * (style.display = "none" o hidden) no se cuentan. Si la página vuelve a
 * generar las filas por JavaScript, la paginación se recalcula sola.
 */
(function () {
    "use strict";

    const TAMANOS = [10, 25, 50, 100];
    const PAGINAS_VISIBLES = 5;

    function crearBoton(contenido, titulo, deshabilitado, actual, accion) {
        const boton = document.createElement("button");
        boton.type = "button";
        boton.className = "pag-btn";
        if (deshabilitado) boton.classList.add("pag-deshabilitado");
        if (actual) boton.classList.add("pag-actual");
        if (titulo) boton.title = titulo;
        if (contenido.indexOf("fa-") === 0) {
            const icono = document.createElement("i");
            icono.className = "fa-solid " + contenido;
            boton.appendChild(icono);
        } else {
            boton.textContent = contenido;
        }
        boton.addEventListener("click", accion);
        return boton;
    }

    function negrita(texto) {
        const strong = document.createElement("strong");
        strong.textContent = texto;
        return strong;
    }

    function iniciar(contenedor) {
        const tabla = contenedor.tagName === "TABLE" ? contenedor : contenedor.querySelector("table");
        if (!tabla || !tabla.tBodies.length) return;

        const cuerpo = tabla.tBodies[0];
        const nombre = contenedor.getAttribute("data-paginar") || "registros";
        let pagina = 1;
        let tamano = 10;
        let totalAnterior = -1;
        let pendiente = false;

        const barra = document.createElement("div");
        barra.className = "clientes-paginacion";
        barra.hidden = true;
        contenedor.insertAdjacentElement("afterend", barra);

        // Filas que cuentan: las que el filtro de la página deja visibles
        function filasValidas() {
            return Array.from(cuerpo.rows).filter(function (fila) {
                if (fila.hidden || fila.style.display === "none") return false;
                if (fila.hasAttribute("data-no-paginar")) return false;
                // Fila de "sin registros" (una sola celda que ocupa todo)
                return !(fila.cells.length === 1 && fila.cells[0].colSpan > 1);
            });
        }

        function dibujar() {
            pendiente = false;
            const filas = filasValidas();
            const total = filas.length;

            if (total !== totalAnterior) {
                pagina = 1;
                totalAnterior = total;
            }

            const totalPaginas = Math.max(1, Math.ceil(total / tamano));
            pagina = Math.min(Math.max(pagina, 1), totalPaginas);
            const inicio = (pagina - 1) * tamano;
            const fin = Math.min(inicio + tamano, total);

            Array.from(cuerpo.rows).forEach(function (fila) {
                fila.classList.remove("pag-fuera");
            });
            filas.forEach(function (fila, indice) {
                if (indice < inicio || indice >= fin) fila.classList.add("pag-fuera");
            });

            barra.replaceChildren();
            barra.hidden = total === 0;
            if (total === 0) return;

            // Mostrando X a Y de N
            const info = document.createElement("span");
            info.className = "pag-info";
            info.append("Mostrando ", negrita(String(inicio + 1)), " a ", negrita(String(fin)),
                    " de ", negrita(String(total)), " " + nombre);

            // Botones
            const nav = document.createElement("nav");
            nav.className = "pag-botones";
            nav.setAttribute("aria-label", "Paginación de " + nombre);

            const ir = function (numero) {
                return function () {
                    pagina = numero;
                    dibujar();
                };
            };

            nav.appendChild(crearBoton("fa-angles-left", "Primera página", pagina === 1, false, ir(1)));
            nav.appendChild(crearBoton("fa-angle-left", "Anterior", pagina === 1, false, ir(pagina - 1)));

            let desde = Math.max(1, pagina - Math.floor(PAGINAS_VISIBLES / 2));
            const hasta = Math.min(totalPaginas, desde + PAGINAS_VISIBLES - 1);
            desde = Math.max(1, hasta - PAGINAS_VISIBLES + 1);
            for (let numero = desde; numero <= hasta; numero++) {
                nav.appendChild(crearBoton(String(numero), null, false, numero === pagina, ir(numero)));
            }

            nav.appendChild(crearBoton("fa-angle-right", "Siguiente", pagina === totalPaginas, false, ir(pagina + 1)));
            nav.appendChild(crearBoton("fa-angles-right", "Última página", pagina === totalPaginas, false, ir(totalPaginas)));

            // Mostrar 10 / 25 / 50 / 100
            const caja = document.createElement("div");
            caja.className = "pag-tamano";
            const etiqueta = document.createElement("label");
            etiqueta.textContent = "Mostrar";
            const selector = document.createElement("select");
            selector.setAttribute("aria-label", "Registros por página");
            TAMANOS.forEach(function (valor) {
                const opcion = document.createElement("option");
                opcion.value = String(valor);
                opcion.textContent = String(valor);
                opcion.selected = valor === tamano;
                selector.appendChild(opcion);
            });
            selector.addEventListener("change", function () {
                tamano = parseInt(selector.value, 10) || 10;
                pagina = 1;
                dibujar();
            });
            etiqueta.addEventListener("click", function () { selector.focus(); });
            const sufijo = document.createElement("span");
            sufijo.textContent = "por página";
            caja.append(etiqueta, selector, sufijo);

            barra.append(info, nav, caja);
        }

        function programar() {
            if (pendiente) return;
            pendiente = true;
            window.requestAnimationFrame(dibujar);
        }

        // Si la página filtra o vuelve a generar filas, se recalcula
        new MutationObserver(programar).observe(cuerpo, {
            childList: true,
            subtree: true,
            attributes: true,
            attributeFilter: ["style", "hidden"]
        });

        dibujar();
    }

    function iniciarTodos() {
        document.querySelectorAll("[data-paginar]").forEach(iniciar);
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", iniciarTodos);
    } else {
        iniciarTodos();
    }
})();
