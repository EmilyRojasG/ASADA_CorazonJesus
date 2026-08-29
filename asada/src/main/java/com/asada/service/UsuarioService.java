package com.asada.service;

import com.asada.domain.Rol;
import com.asada.domain.Usuario;
import com.asada.repository.RolRepository;
import com.asada.repository.UsuarioRepository;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final ImageStorageService imageStorageService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            ImageStorageService imageStorageService,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.imageStorageService = imageStorageService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean soloActivos) {
        if (soloActivos) {
            return usuarioRepository.findByActivoTrueConRoles();
        }
        return usuarioRepository.findAllConRoles();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Integer idUsuario) {
        return usuarioRepository.findByIdConRoles(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Rol> getRoles() {
        return rolRepository.findAll();
    }

    /**
     * Crea o actualiza un usuario.
     *
     * @param usuario datos del formulario (la contraseña viene en texto plano
     * o vacía si no se desea cambiar).
     * @param imagenFile imagen de perfil opcional.
     * @param idsRoles identificadores de los roles seleccionados en el
     * formulario.
     */
    @Transactional
    public void save(Usuario usuario, MultipartFile imagenFile, Set<Integer> idsRoles) {

        boolean esNuevo = usuario.getIdUsuario() == null;
        final Integer idUsuarioActual = usuario.getIdUsuario();

        // Verifica duplicados de username/correo excluyendo al propio usuario
        usuarioRepository.findByUsername(usuario.getUsername()).ifPresent(existente -> {
            if (esNuevo || !existente.getIdUsuario().equals(idUsuarioActual)) {
                throw new IllegalArgumentException("Ya existe un usuario con ese nombre de usuario.");
            }
        });

        if (esNuevo) {
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria para usuarios nuevos.");
            }
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        } else {
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                // No se desea cambiar la contraseña: se conserva la existente.
                Usuario existente = usuarioRepository.findById(usuario.getIdUsuario())
                        .orElseThrow(() -> new IllegalArgumentException("Usuario a modificar no encontrado."));
                usuario.setPassword(existente.getPassword());
                if (usuario.getRutaImagen() == null) {
                    usuario.setRutaImagen(existente.getRutaImagen());
                }
            } else {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
        }

        Set<Rol> roles = new HashSet<>();
        if (idsRoles != null) {
            for (Integer idRol : idsRoles) {
                rolRepository.findById(idRol).ifPresent(roles::add);
            }
        }
        usuario.setRoles(roles);

        try {
            usuario = usuarioRepository.save(usuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese nombre de usuario o correo.", e);
        }

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String rutaImagen = imageStorageService.uploadImage(imagenFile, "usuario", usuario.getIdUsuario());
                usuario.setRutaImagen(rutaImagen);
                usuarioRepository.save(usuario);
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo guardar la imagen del usuario.", e);
            }
        }
    }

    /**
     * Permite a un usuario ya autenticado cambiar únicamente su propia
     * foto de perfil, sin tener que pasar por el formulario completo de
     * edición (que además requiere permiso EDITAR).
     */
    @Transactional
    public void cambiarFotoPerfil(String username, MultipartFile imagenFile) {
        if (imagenFile == null || imagenFile.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar una imagen.");
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no fue encontrado."));

        try {
            String rutaImagen = imageStorageService.uploadImage(imagenFile, "usuario", usuario.getIdUsuario());
            usuario.setRutaImagen(rutaImagen);
            usuarioRepository.save(usuario);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar la nueva foto de perfil.", e);
        }
    }

    /**
     * Permite a un usuario (autenticado o no, por ejemplo desde "olvidé mi
     * contraseña" en el login) cambiar su propia contraseña, verificando
     * primero la contraseña actual.
     */
    @Transactional
    public void cambiarPassword(String username, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no fue encontrado."));

        if (!usuario.isActivo()) {
            throw new IllegalArgumentException("El usuario está inactivo. Contacte a un administrador.");
        }

        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta.");
        }

        if (passwordNueva == null || passwordNueva.isBlank() || passwordNueva.length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres.");
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void delete(Integer idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException("El usuario con ID " + idUsuario + " no existe.");
        }
        try {
            usuarioRepository.deleteById(idUsuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar el usuario: tiene lecturas registradas. "
                    + "Puede desactivarlo en su lugar.", e);
        }
    }

}
