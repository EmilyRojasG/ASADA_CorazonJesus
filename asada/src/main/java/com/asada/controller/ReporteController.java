package com.asada.controller;

import com.asada.domain.Lectura;
import com.asada.repository.AbonadoRepository;
import com.asada.service.ReporteService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Reportes de consumo de agua: historial por abonado, consumo mensual y
 * filtros por período, con exportación a CSV. Accesible para cualquier
 * usuario autenticado, sin importar su rol.
 */
@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reporteService;
    private final AbonadoRepository abonadoRepository;

    public ReporteController(ReporteService reporteService, AbonadoRepository abonadoRepository) {
        this.reporteService = reporteService;
        this.abonadoRepository = abonadoRepository;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer idAbonado,
            Model model) {

        int anioConsulta = anio != null ? anio : LocalDate.now().getYear();

        model.addAttribute("abonados", abonadoRepository.findByActivoTrue());
        model.addAttribute("anioSeleccionado", anioConsulta);
        model.addAttribute("mesSeleccionado", mes);
        model.addAttribute("idAbonadoSeleccionado", idAbonado);
        model.addAttribute("seBusco", anio != null);

        if (anio != null) {
            List<Lectura> lecturas = reporteService.buscar(anioConsulta, mes, idAbonado);
            model.addAttribute("lecturas", lecturas);
            model.addAttribute("resumen", reporteService.calcularResumen(lecturas));
            model.addAttribute("consumoPorMes", reporteService.consumoPorMes(lecturas));
            model.addAttribute("reporteService", reporteService);
        }

        return "reportes/listado";
    }

    @GetMapping("/exportar.csv")
    public void exportarCsv(
            @RequestParam Integer anio,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer idAbonado,
            HttpServletResponse response) throws Exception {

        List<Lectura> lecturas = reporteService.buscar(anio, mes, idAbonado);

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_consumo_" + anio + ".csv");
        response.getOutputStream().write(0xEF);
        response.getOutputStream().write(0xBB);
        response.getOutputStream().write(0xBF); // BOM para que Excel detecte UTF-8

        try (PrintWriter writer = new PrintWriter(response.getOutputStream(), true, StandardCharsets.UTF_8)) {
            writer.println("Numero Abonado,Nombre,Periodo,Fecha Lectura,Lectura Anterior,Lectura Actual,Consumo m3,Monto Estimado");
            for (Lectura lectura : lecturas) {
                var montoEstimado = reporteService.calcularMontoEstimado(lectura);
                writer.println(
                        lectura.getAbonado().getNumeroAbonado() + ","
                        + "\"" + lectura.getAbonado().getNombre() + " " + lectura.getAbonado().getApellidos() + "\","
                        + lectura.getPeriodoMes() + "/" + lectura.getPeriodoAnio() + ","
                        + lectura.getFechaLectura() + ","
                        + lectura.getLecturaAnterior() + ","
                        + lectura.getLecturaActual() + ","
                        + lectura.getConsumo() + ","
                        + montoEstimado
                );
            }
        }
    }

}
