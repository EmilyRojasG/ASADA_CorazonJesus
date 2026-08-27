package com.asada.controller;

import com.asada.domain.Usuario;
import com.asada.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var usuarios = usuarioService.getUsuarios(false);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        return "usuario/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Usuario usuario = new Usuario();
        usuario.setActivo(true);
        model.addAttribute("usuario", usuario);
        model.addAttribute("todosLosRoles", usuarioService.getRoles());
        model.addAttribute("idsRolesSeleccionados", Set.of());
        return "usuario/formulario";
    }

    @GetMapping("/modificar/{idUsuario}")
    public String modificar(@PathVariable Integer idUsuario, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.getUsuario(idUsuario);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El usuario no fue encontrado.");
            return "redirect:/usuario/listado";
        }
        Usuario usuario = usuarioOpt.get();
        Set<Integer> idsRolesSeleccionados = new HashSet<>();
        usuario.getRoles().forEach(rol -> idsRolesSeleccionados.add(rol.getIdRol()));

        // No se muestra el hash de la contraseña en el formulario.
        usuario.setPassword("");

        model.addAttribute("usuario", usuario);
        model.addAttribute("todosLosRoles", usuarioService.getRoles());
        model.addAttribute("idsRolesSeleccionados", idsRolesSeleccionados);
        return "usuario/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("usuario") Usuario usuario,
            BindingResult bindingResult,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            @RequestParam(value = "idsRoles", required = false) Set<Integer> idsRoles,
            Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("todosLosRoles", usuarioService.getRoles());
            model.addAttribute("idsRolesSeleccionados", idsRoles == null ? Set.of() : idsRoles);
            return "usuario/formulario";
        }

        try {
            usuarioService.save(usuario, imagenFile, idsRoles);
            redirectAttributes.addFlashAttribute("todoOk", "Usuario guardado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (usuario.getIdUsuario() != null) {
                return "redirect:/usuario/modificar/" + usuario.getIdUsuario();
            }
            return "redirect:/usuario/nuevo";
        }

        return "redirect:/usuario/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idUsuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.delete(idUsuario);
            redirectAttributes.addFlashAttribute("todoOk", "Usuario eliminado correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/usuario/listado";
    }

}
