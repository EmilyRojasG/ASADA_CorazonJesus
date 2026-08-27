document.addEventListener("DOMContentLoaded", function () {

    console.log("Sistema ASADA Barrio Corazón de Jesús iniciado.");

    // Cierra automáticamente las alertas/toasts después de unos segundos
    document.querySelectorAll(".alert.alert-dismissible-auto").forEach(function (alerta) {
        setTimeout(function () {
            var instancia = bootstrap.Alert.getOrCreateInstance(alerta);
            instancia.close();
        }, 5000);
    });

    // Activa los tooltips de Bootstrap si existen en la página
    var tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    tooltipTriggerList.forEach(function (el) {
        new bootstrap.Tooltip(el);
    });

});
