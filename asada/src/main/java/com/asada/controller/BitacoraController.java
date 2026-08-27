package com.asada.controller;

import com.asada.service.ActividadFontaneroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Bitácora de auditoría de las actividades del fontanero: quién hizo qué
 * y cuándo, incluyendo el motivo de cada edición o eliminación.
 */
@Controller
public class BitacoraController {

    private final ActividadFontaneroService actividadFontaneroService;

    public BitacoraController(ActividadFontaneroService actividadFontaneroService) {
        this.actividadFontaneroService = actividadFontaneroService;
    }

    @GetMapping("/bitacora/listado")
    public String listado(Model model) {
        model.addAttribute("entradas", actividadFontaneroService.getBitacora());
        return "bitacora/listado";
    }

}
