package com.asada.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Representa un rol de seguridad del sistema (ADMIN, SECRETARIA, etc.).
 */
@Entity
@Table(name = "rol")
public class Rol implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "rol", unique = true, nullable = false, length = 20)
    private String rol;

    public Rol() {
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rol)) {
            return false;
        }
        Rol other = (Rol) o;
        return idRol != null && idRol.equals(other.idRol);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idRol);
    }

    @Override
    public String toString() {
        return "Rol{idRol=" + idRol + ", rol=" + rol + '}';
    }

}
