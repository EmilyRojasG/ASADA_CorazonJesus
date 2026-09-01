package com.asada.controller;

import com.asada.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Pantalla de "olvidé mi contraseña", accesible desde el login sin estar
 * autenticado.
 *
 * Como el sistema no tiene un servicio de correo (SMTP) configurado, no
 * es posible enviar un enlace de restablecimiento por email. En su lugar,
 * esta pantalla es informativa: le indica a la persona que contacte a un
 * administrador (alguien con permiso EDITAR), quien puede asignarle una
 * nueva contraseña desde Usuarios -> Editar sin necesitar la anterior
 * (esa función ya existe en UsuarioController/UsuarioService).
 */
@Controller
public class PasswordController {

    private final UsuarioRepository usuarioRepository;

    public PasswordController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/login/actualizar-password")
    public String informacion(Model model) {
        model.addAttribute("administradores", usuarioRepository.findByRolYActivoTrue("EDITAR"));
        return "login-actualizar-password";
    }

}
