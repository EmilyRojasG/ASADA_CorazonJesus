package com.asada.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Categoría tarifaria del acueducto (Residencial, Comercial, Institucional, etc.).
 */
@Entity
@Table(name = "categoria_tarifa")
public class CategoriaTarifa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria_tarifa")
    private Integer idCategoriaTarifa;

    @NotBlank
    @Column(nullable = false, unique = true, length = 60)
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "tarifa_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal tarifaBase;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "precio_m3", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioM3;

    private boolean activo;

    public CategoriaTarifa() {
    }

    public Integer getIdCategoriaTarifa() {
        return idCategoriaTarifa;
    }

    public void setIdCategoriaTarifa(Integer idCategoriaTarifa) {
        this.idCategoriaTarifa = idCategoriaTarifa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getTarifaBase() {
        return tarifaBase;
    }

    public void setTarifaBase(BigDecimal tarifaBase) {
        this.tarifaBase = tarifaBase;
    }

    public BigDecimal getPrecioM3() {
        return precioM3;
    }

    public void setPrecioM3(BigDecimal precioM3) {
        this.precioM3 = precioM3;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoriaTarifa)) {
            return false;
        }
        CategoriaTarifa other = (CategoriaTarifa) o;
        return idCategoriaTarifa != null && idCategoriaTarifa.equals(other.idCategoriaTarifa);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idCategoriaTarifa);
    }

    @Override
    public String toString() {
        return "CategoriaTarifa{idCategoriaTarifa=" + idCategoriaTarifa
                + ", descripcion=" + descripcion + '}';
    }

}
