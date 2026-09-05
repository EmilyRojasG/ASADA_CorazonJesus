package com.asada.controller;

import com.asada.domain.Usuario;
import com.asada.repository.UsuarioRepository;
import com.asada.service.ArchivoCartaService;
import com.asada.service.CartaDisponibilidadService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carta_disponibilidad")
public class CartaDisponibilidadController {

    private final CartaDisponibilidadService cartaDisponibilidadService;
    private final ArchivoCartaService archivoCartaService;
    private final UsuarioRepository usuarioRepository;

    public CartaDisponibilidadController(CartaDisponibilidadService cartaDisponibilidadService,
            ArchivoCartaService archivoCartaService,
            UsuarioRepository usuarioRepository) {
        this.cartaDisponibilidadService = cartaDisponibilidadService;
        this.archivoCartaService = archivoCartaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var cartas = cartaDisponibilidadService.getCartas();
        model.addAttribute("cartas", cartas);
        model.addAttribute("totalCartas", cartas.size());
        model.addAttribute("archivosPorCarta", archivoCartaService.getArchivosAgrupadosPorCarta());
        return "carta_disponibilidad/listado";
    }

    @GetMapping("/nueva")
    public String nueva() {
        return "carta_disponibilidad/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam String nombreSolicitante,
            @RequestParam String cedulaSolicitante,
            @RequestParam String direccionPropiedad,
            @RequestParam(required = false) String numeroFinca,
            @RequestParam(required = false) String planoCatastrado,
            @RequestParam(required = false) String observaciones,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioRepository.findByUsername(principal.getName()).orElse(null);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "No se pudo identificar al usuario.");
            return "redirect:/carta_disponibilidad/nueva";
        }

        try {
            var carta = cartaDisponibilidadService.emitir(usuario, nombreSolicitante, cedulaSolicitante,
                    direccionPropiedad, numeroFinca, planoCatastrado, observaciones);

            redirectAttributes.addFlashAttribute("todoOk",
                    "Carta " + carta.getNumeroCarta() + " generada correctamente.");
            return "redirect:/carta_disponibilidad/ver/" + carta.getIdCarta();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/carta_disponibilidad/nueva";
        }
    }

    @GetMapping("/ver/{idCarta}")
    public String ver(@PathVariable Integer idCarta, Model model, RedirectAttributes redirectAttributes) {
        var cartaOpt = cartaDisponibilidadService.getCarta(idCarta);
        if (cartaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La carta no fue encontrada.");
            return "redirect:/carta_disponibilidad/listado";
        }
        model.addAttribute("carta", cartaOpt.get());
        return "carta_disponibilidad/ver";
    }

    @PostMapping("/{idCarta}/archivos/subir")
    public String subirArchivos(@PathVariable Integer idCarta,
            @RequestParam("archivos") MultipartFile[] archivos,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioRepository.findByUsername(principal.getName()).orElse(null);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "No se pudo identificar al usuario.");
            return "redirect:/carta_disponibilidad/listado";
        }

        try {
            archivoCartaService.subirArchivos(idCarta, usuario, archivos);
            redirectAttributes.addFlashAttribute("todoOk", "Archivo(s) guardado(s) correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/carta_disponibilidad/listado";
    }

}
