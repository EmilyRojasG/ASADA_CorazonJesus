package com.asada.controller;

import com.asada.domain.CategoriaTarifa;
import com.asada.security.PermisoUtil;
import com.asada.service.CategoriaTarifaService;
import jakarta.validation.Valid;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categoria_tarifa")
public class CategoriaTarifaController {

    private final CategoriaTarifaService categoriaTarifaService;

    public CategoriaTarifaController(CategoriaTarifaService categoriaTarifaService) {
        this.categoriaTarifaService = categoriaTarifaService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var categorias = categoriaTarifaService.getCategorias(false);
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalCategorias", categorias.size());
        return "categoria/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        CategoriaTarifa categoriaTarifa = new CategoriaTarifa();
        categoriaTarifa.setActivo(true);
        model.addAttribute("categoriaTarifa", categoriaTarifa);
        return "categoria/formulario";
    }

    @GetMapping("/modificar/{idCategoriaTarifa}")
    public String modificar(@PathVariable Integer idCategoriaTarifa, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<CategoriaTarifa> categoriaOpt = categoriaTarifaService.getCategoria(idCategoriaTarifa);
        if (categoriaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "La categoría tarifaria no fue encontrada.");
            return "redirect:/categoria_tarifa/listado";
        }
        model.addAttribute("categoriaTarifa", categoriaOpt.get());
        return "categoria/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("categoriaTarifa") CategoriaTarifa categoriaTarifa,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "categoria/formulario";
        }

        PermisoUtil.requiereRol(categoriaTarifa.getIdCategoriaTarifa() == null ? "AGREGAR" : "EDITAR");

        try {
            categoriaTarifaService.save(categoriaTarifa);
            redirectAttributes.addFlashAttribute("todoOk", "Categoría tarifaria guardada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (categoriaTarifa.getIdCategoriaTarifa() != null) {
                return "redirect:/categoria_tarifa/modificar/" + categoriaTarifa.getIdCategoriaTarifa();
            }
            return "redirect:/categoria_tarifa/nuevo";
        }

        return "redirect:/categoria_tarifa/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idCategoriaTarifa, RedirectAttributes redirectAttributes) {
        try {
            categoriaTarifaService.delete(idCategoriaTarifa);
            redirectAttributes.addFlashAttribute("todoOk", "Categoría tarifaria eliminada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categoria_tarifa/listado";
    }

}
