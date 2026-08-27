package com.asada.controller;

import com.asada.domain.Abonado;
import com.asada.service.AbonadoService;
import com.asada.service.CategoriaTarifaService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/abonado")
public class AbonadoController {

    private final AbonadoService abonadoService;
    private final CategoriaTarifaService categoriaTarifaService;

    public AbonadoController(AbonadoService abonadoService, CategoriaTarifaService categoriaTarifaService) {
        this.abonadoService = abonadoService;
        this.categoriaTarifaService = categoriaTarifaService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var abonados = abonadoService.getAbonados(false);
        model.addAttribute("abonados", abonados);
        model.addAttribute("totalAbonados", abonados.size());
        return "abonado/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Abonado abonado = new Abonado();
        abonado.setActivo(true);
        abonado.setFechaIngreso(LocalDate.now());
        model.addAttribute("abonado", abonado);
        model.addAttribute("categorias", categoriaTarifaService.getCategorias(true));
        return "abonado/formulario";
    }

    @GetMapping("/modificar/{idAbonado}")
    public String modificar(@PathVariable Integer idAbonado, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Abonado> abonadoOpt = abonadoService.getAbonado(idAbonado);
        if (abonadoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El abonado no fue encontrado.");
            return "redirect:/abonado/listado";
        }
        model.addAttribute("abonado", abonadoOpt.get());
        model.addAttribute("categorias", categoriaTarifaService.getCategorias(true));
        return "abonado/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("abonado") Abonado abonado,
            BindingResult bindingResult,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categorias", categoriaTarifaService.getCategorias(true));
            return "abonado/formulario";
        }

        try {
            abonadoService.save(abonado, imagenFile);
            redirectAttributes.addFlashAttribute("todoOk", "Abonado guardado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (abonado.getIdAbonado() != null) {
                return "redirect:/abonado/modificar/" + abonado.getIdAbonado();
            }
            return "redirect:/abonado/nuevo";
        }

        return "redirect:/abonado/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idAbonado, RedirectAttributes redirectAttributes) {
        try {
            abonadoService.delete(idAbonado);
            redirectAttributes.addFlashAttribute("todoOk", "Abonado eliminado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/abonado/listado";
    }

}
