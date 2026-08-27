package com.asada.controller;

import com.asada.domain.Abonado;
import com.asada.domain.Usuario;
import com.asada.repository.AbonadoRepository;
import com.asada.repository.UsuarioRepository;
import com.asada.service.LecturaService;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/lectura")
public class LecturaController {

    private final LecturaService lecturaService;
    private final AbonadoRepository abonadoRepository;
    private final UsuarioRepository usuarioRepository;

    public LecturaController(LecturaService lecturaService,
            AbonadoRepository abonadoRepository,
            UsuarioRepository usuarioRepository) {
        this.lecturaService = lecturaService;
        this.abonadoRepository = abonadoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var lecturas = lecturaService.getLecturas();
        model.addAttribute("lecturas", lecturas);
        model.addAttribute("totalLecturas", lecturas.size());
        return "lectura/listado";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        var abonados = abonadoRepository.findByActivoTrue();
        Map<Integer, BigDecimal> ultimasLecturas = new HashMap<>();
        for (var abonado : abonados) {
            BigDecimal ultima = lecturaService.getUltimaLectura(abonado.getIdAbonado())
                    .map(l -> l.getLecturaActual())
                    .orElse(BigDecimal.ZERO);
            ultimasLecturas.put(abonado.getIdAbonado(), ultima);
        }
        model.addAttribute("abonados", abonados);
        model.addAttribute("ultimasLecturas", ultimasLecturas);
        model.addAttribute("hoy", LocalDate.now());
        return "lectura/formulario";
    }

    @GetMapping("/historial/{idAbonado}")
    public String historial(@PathVariable Integer idAbonado, Model model,
            RedirectAttributes redirectAttributes) {
        var abonadoOpt = abonadoRepository.findById(idAbonado);
        if (abonadoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El abonado no fue encontrado.");
            return "redirect:/lectura/listado";
        }
        model.addAttribute("abonado", abonadoOpt.get());
        model.addAttribute("lecturas", lecturaService.getHistorial(idAbonado));
        return "lectura/historial";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam Integer idAbonado,
            @RequestParam Integer periodoAnio,
            @RequestParam Integer periodoMes,
            @RequestParam LocalDate fechaLectura,
            @RequestParam BigDecimal lecturaActual,
            @RequestParam(required = false) String observaciones,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Abonado abonado = abonadoRepository.findById(idAbonado).orElse(null);
        if (abonado == null) {
            redirectAttributes.addFlashAttribute("error", "El abonado seleccionado no fue encontrado.");
            return "redirect:/lectura/nueva";
        }

        Usuario usuario = usuarioRepository.findByUsername(principal.getName()).orElse(null);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "No se pudo identificar al usuario que registra la lectura.");
            return "redirect:/lectura/nueva";
        }

        try {
            lecturaService.registrar(abonado, usuario, periodoAnio, periodoMes, fechaLectura, lecturaActual, observaciones);
            redirectAttributes.addFlashAttribute("todoOk", "Lectura registrada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/lectura/nueva";
        }

        return "redirect:/lectura/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idLectura, RedirectAttributes redirectAttributes) {
        try {
            lecturaService.delete(idLectura);
            redirectAttributes.addFlashAttribute("todoOk", "Lectura eliminada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/lectura/listado";
    }

}
