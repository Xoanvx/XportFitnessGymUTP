// Comportamiento de CambiarClave.xhtml
(function () {
    // Mostrar / ocultar contraseña (delegado: funciona tras el render ajax)
    document.addEventListener("click", function (e) {
        var btn = e.target.closest(".ver-clave");
        if (!btn) return;
        var input = document.getElementById(btn.getAttribute("data-objetivo"));
        if (!input) return;
        var mostrar = input.type === "password";
        input.type = mostrar ? "text" : "password";
        btn.innerHTML = mostrar ? '<i class="fa-solid fa-eye-slash"></i>' : '<i class="fa-solid fa-eye"></i>';
        btn.title = mostrar ? "Ocultar contraseña" : "Mostrar contraseña";
    });

    var nueva = document.getElementById("claveNueva");
    var confirma = document.getElementById("claveConfirmacion");
    var fuerza = document.getElementById("fuerzaClave");
    var texto = document.getElementById("textoFuerza");
    var coincide = document.getElementById("coincideClave");
    var textos = ["Use letras y números. No distingue mayúsculas de minúsculas.",
                  "Seguridad: débil", "Seguridad: regular", "Seguridad: buena", "Seguridad: fuerte"];

    function nivel(v) {
        if (!v) return 0;
        var n = 0;
        if (v.length >= 6) n++;
        if (v.length >= 10) n++;
        if (/[a-zA-Z]/.test(v) && /\d/.test(v)) n++;
        if (/[^a-zA-Z0-9]/.test(v)) n++;
        return Math.max(1, Math.min(4, n));
    }
    function actualizar() {
        var n = nivel(nueva.value);
        fuerza.setAttribute("data-nivel", n);
        texto.textContent = textos[n];
        if (!confirma.value) { coincide.textContent = ""; coincide.className = "coincide"; return; }
        var ok = confirma.value === nueva.value;
        coincide.textContent = ok ? "✔ Las contraseñas coinciden" : "✖ Las contraseñas no coinciden";
        coincide.className = "coincide " + (ok ? "ok" : "no");
    }
    if (nueva && confirma) {
        nueva.addEventListener("input", actualizar);
        confirma.addEventListener("input", actualizar);
    }
}());
