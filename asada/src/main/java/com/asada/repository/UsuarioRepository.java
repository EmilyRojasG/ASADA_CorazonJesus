package com.asada.repository;

import com.asada.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsernameAndActivoTrue(String username);

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByActivoTrue();

    boolean existsByUsernameOrCorreo(String username, String correo);

    /**
     * Trae los usuarios junto con sus roles ya inicializados (fetch join),
     * necesario porque las vistas se renderizan fuera de la transacción
     * (spring.jpa.open-in-view=false) y "roles" es una colección LAZY.
     */
    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles ORDER BY u.username")
    List<Usuario> findAllConRoles();

    @Query("SELECT DISTINCT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.activo = true ORDER BY u.username")
    List<Usuario> findByActivoTrueConRoles();

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.idUsuario = :idUsuario")
    Optional<Usuario> findByIdConRoles(@Param("idUsuario") Integer idUsuario);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<Usuario> findByUsernameConRoles(@Param("username") String username);

}
