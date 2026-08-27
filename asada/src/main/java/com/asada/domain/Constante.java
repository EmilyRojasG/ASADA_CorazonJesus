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
 * Constantes de configuración de la aplicación almacenadas en base de datos
 * (nombre de la ASADA, dominio, URL del servidor, etc.).
 */
@Entity
@Table(name = "constante")
public class Constante implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_constante")
    private Integer idConstante;

    @Column(name = "atributo", unique = true, nullable = false, length = 25)
    private String atributo;

    @Column(name = "valor", nullable = false, length = 150)
    private String valor;

    public Constante() {
    }

    public Constante(Integer idConstante, String atributo, String valor) {
        this.idConstante = idConstante;
        this.atributo = atributo;
        this.valor = valor;
    }

    public Integer getIdConstante() {
        return idConstante;
    }

    public void setIdConstante(Integer idConstante) {
        this.idConstante = idConstante;
    }

    public String getAtributo() {
        return atributo;
    }

    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Constante)) {
            return false;
        }
        Constante other = (Constante) o;
        return idConstante != null && idConstante.equals(other.idConstante);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idConstante);
    }

    @Override
    public String toString() {
        return "Constante{idConstante=" + idConstante + ", atributo=" + atributo + '}';
    }

}
