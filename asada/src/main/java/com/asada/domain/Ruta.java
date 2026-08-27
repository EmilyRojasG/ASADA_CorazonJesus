package com.asada.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Ruta protegida del sistema. Permite definir en base de datos qué rol
 * puede acceder a cada patrón de URL (seguridad dinámica), tal como en
 * el proyecto de referencia tienda_vm.
 */
@Entity
@Table(name = "ruta")
public class Ruta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Integer idRuta;

    @Column(name = "ruta", nullable = false, length = 255)
    private String ruta;

    @Column(name = "requiere_rol", nullable = false)
    private boolean requiereRol = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol")
    private Rol rol;

    public Ruta() {
    }

    public Integer getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(Integer idRuta) {
        this.idRuta = idRuta;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public boolean isRequiereRol() {
        return requiereRol;
    }

    public void setRequiereRol(boolean requiereRol) {
        this.requiereRol = requiereRol;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ruta)) {
            return false;
        }
        Ruta other = (Ruta) o;
        return idRuta != null && idRuta.equals(other.idRuta);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idRuta);
    }

    @Override
    public String toString() {
        return "Ruta{idRuta=" + idRuta + ", ruta=" + ruta + '}';
    }

}
