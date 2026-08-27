package com.asada.controller;

import com.asada.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Permite actualizar la contraseña desde la pantalla de login, sin
 * necesidad de estar autenticado, verificando la contraseña actual del
 * usuario (no requiere correo/SMTP configurado).
 */
@Controller
public class PasswordController {

    private final UsuarioService usuarioService;

    public PasswordController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login/actualizar-password")
    public String formulario() {
        return "login-actualizar-password";
    }

    @PostMapping("/login/actualizar-password")
    public String actualizar(@RequestParam String username,
            @RequestParam String passwordActual,
            @RequestParam String passwordNueva,
            @RequestParam String passwordConfirmar,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!passwordNueva.equals(passwordConfirmar)) {
            model.addAttribute("error", "La nueva contraseña y su confirmación no coinciden.");
            model.addAttribute("username", username);
            return "login-actualizar-password";
        }

        try {
            usuarioService.cambiarPassword(username, passwordActual, passwordNueva);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("username", username);
            return "login-actualizar-password";
        }

        redirectAttributes.addFlashAttribute("passwordActualizada", true);
        return "redirect:/login";
    }

}
