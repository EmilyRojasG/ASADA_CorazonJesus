package com.asada.controller;

import com.asada.repository.UsuarioRepository;
import com.asada.service.UsuarioService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Perfil del usuario que ha iniciado sesión: información básica y cambio
 * de su propia contraseña.
 */
@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public PerfilController(UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String perfil(Principal principal, Model model) {
        var usuario = usuarioRepository.findByUsernameConRoles(principal.getName()).orElse(null);
        model.addAttribute("usuario", usuario);
        return "perfil/perfil";
    }

    @PostMapping("/cambiar-foto")
    public String cambiarFoto(Principal principal,
            @RequestParam("imagenFile") MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {

        try {
            usuarioService.cambiarFotoPerfil(principal.getName(), imagenFile);
            redirectAttributes.addFlashAttribute("todoOk", "Foto de perfil actualizada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/perfil";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(Principal principal,
            @RequestParam String passwordActual,
            @RequestParam String passwordNueva,
            @RequestParam String passwordConfirmar,
            RedirectAttributes redirectAttributes) {

        if (!passwordNueva.equals(passwordConfirmar)) {
            redirectAttributes.addFlashAttribute("error", "La nueva contraseña y su confirmación no coinciden.");
            return "redirect:/perfil";
        }

        try {
            usuarioService.cambiarPassword(principal.getName(), passwordActual, passwordNueva);
            redirectAttributes.addFlashAttribute("todoOk", "Contraseña actualizada correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/perfil";
    }

}
