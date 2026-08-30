package com.asada.controller;

import com.asada.domain.Abonado;
import com.asada.domain.ActividadFontanero;
import com.asada.domain.Usuario;
import com.asada.repository.AbonadoRepository;
import com.asada.repository.UsuarioRepository;
import com.asada.service.ActividadFontaneroService;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Registro de actividades del fontanero. Solo el rol FONTANERO puede
 * agregar, editar o eliminar registros (ver tabla `ruta`); cada acción
 * queda anotada en la bitácora de auditoría.
 */
@Controller
@RequestMapping("/actividad")
public class ActividadFontaneroController {

    private static final List<String> TIPOS_ACTIVIDAD = List.of(
            "Instalación de medidor",
            "Reparación de fuga",
            "Mantenimiento preventivo",
            "Corte de servicio",
            "Reconexión de servicio",
            "Revisión de tubería",
            "Otro"
    );

    private final ActividadFontaneroService actividadFontaneroService;
    private final AbonadoRepository abonadoRepository;
    private final UsuarioRepository usuarioRepository;

    public ActividadFontaneroController(ActividadFontaneroService actividadFontaneroService,
            AbonadoRepository abonadoRepository,
            UsuarioRepository usuarioRepository) {
        this.actividadFontaneroService = actividadFontaneroService;
        this.abonadoRepository = abonadoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var actividades = actividadFontaneroService.getActividades();
        model.addAttribute("actividades", actividades);
        model.addAttribute("totalActividades", actividades.size());
        return "actividad/listado";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("abonados", abonadoRepository.findByActivoTrue());
        model.addAttribute("tiposActividad", TIPOS_ACTIVIDAD);
        model.addAttribute("hoy", LocalDate.now());
        return "actividad/formulario";
    }

    @GetMapping("/modificar/{idActividad}")
    public String modificar(@PathVariable Integer idActividad, Model model,
            RedirectAttributes redirectAttributes) {
        var actividadOpt = actividadFontaneroService.getActividad(idActividad);
        if (actividadOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La actividad no fue encontrada.");
            return "redirect:/actividad/listado";
        }
        model.addAttribute("actividad", actividadOpt.get());
        model.addAttribute("abonados", abonadoRepository.findByActivoTrue());
        model.addAttribute("tiposActividad", TIPOS_ACTIVIDAD);
        return "actividad/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam(required = false) Integer idActividad,
            @RequestParam(required = false) Integer idAbonado,
            @RequestParam String tipoActividad,
            @RequestParam String descripcion,
            @RequestParam LocalDate fechaActividad,
            @RequestParam(required = false) BigDecimal horasTrabajadas,
            @RequestParam(required = false) String motivo,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario fontanero = usuarioRepository.findByUsername(principal.getName()).orElse(null);
        if (fontanero == null) {
            redirectAttributes.addFlashAttribute("error", "No se pudo identificar al usuario.");
            return "redirect:/actividad/listado";
        }

        Abonado abonado = idAbonado != null ? abonadoRepository.findById(idAbonado).orElse(null) : null;

        try {
            if (idActividad == null) {
                actividadFontaneroService.registrar(fontanero, abonado, tipoActividad, descripcion,
                        fechaActividad, horasTrabajadas);
                redirectAttributes.addFlashAttribute("todoOk", "Actividad registrada correctamente.");
            } else {
                actividadFontaneroService.modificar(fontanero, idActividad, abonado, tipoActividad,
                        descripcion, fechaActividad, horasTrabajadas, motivo);
                redirectAttributes.addFlashAttribute("todoOk", "Actividad modificada correctamente.");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (idActividad != null) {
                return "redirect:/actividad/modificar/" + idActividad;
            }
            return "redirect:/actividad/nueva";
        }

        return "redirect:/actividad/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idActividad,
            @RequestParam String motivo,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario fontanero = usuarioRepository.findByUsername(principal.getName()).orElse(null);
        if (fontanero == null) {
            redirectAttributes.addFlashAttribute("error", "No se pudo identificar al usuario.");
            return "redirect:/actividad/listado";
        }

        try {
            actividadFontaneroService.eliminar(fontanero, idActividad, motivo);
            redirectAttributes.addFlashAttribute("todoOk", "Actividad eliminada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/actividad/listado";
    }

}
