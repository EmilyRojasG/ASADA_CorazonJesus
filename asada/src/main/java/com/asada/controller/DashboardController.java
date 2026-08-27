package com.asada.controller;

import com.asada.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Panel principal del sistema, visible para cualquier usuario autenticado
 * (ADMIN o SECRETARIA), con los indicadores clave de la operación diaria.
 */
@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("resumen", dashboardService.getResumen());
        return "dashboard/dashboard";
    }

}
