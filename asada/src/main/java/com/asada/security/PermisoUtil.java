package com.asada.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Verificaciones de permiso adicionales, más finas que las reglas por URL
 * de la tabla `ruta`. Se usan sobre todo en los métodos "guardar" que
 * atienden tanto la creación como la edición bajo la misma URL: la tabla
 * `ruta` solo puede exigir "AGREGAR o EDITAR" a nivel de URL, así que aquí
 * se valida cuál de los dos aplica según si el registro es nuevo o no.
 */
public final class PermisoUtil {

    private PermisoUtil() {
    }

    public static void requiereRol(String rol) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean tieneRol = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + rol));
        if (!tieneRol) {
            throw new AccessDeniedException(
                    "No tiene permiso de '" + rol + "' para realizar esta acción.");
        }
    }

}
