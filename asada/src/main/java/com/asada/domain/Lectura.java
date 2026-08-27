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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Lectura mensual del medidor de un abonado. El consumo se calcula
 * como lecturaActual - lecturaAnterior.
 */
@Entity
@Table(name = "lectura",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_lectura_abonado_periodo",
                columnNames = {"id_abonado", "periodo_anio", "periodo_mes"}))
public class Lectura implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lectura")
    private Integer idLectura;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_abonado", nullable = false)
    private Abonado abonado;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @NotNull
    @Column(name = "periodo_anio", nullable = false)
    private Integer periodoAnio;

    @NotNull
    @Column(name = "periodo_mes", nullable = false)
    private Integer periodoMes;

    @NotNull
    @Column(name = "fecha_lectura", nullable = false)
    private LocalDate fechaLectura;

    @NotNull
    @Column(name = "lectura_anterior", nullable = false, precision = 12, scale = 2)
    private BigDecimal lecturaAnterior;

    @NotNull
    @Column(name = "lectura_actual", nullable = false, precision = 12, scale = 2)
    private BigDecimal lecturaActual;

    @Column(length = 255)
    private String observaciones;

    public Lectura() {
    }

    public Integer getIdLectura() {
        return idLectura;
    }

    public void setIdLectura(Integer idLectura) {
        this.idLectura = idLectura;
    }

    public Abonado getAbonado() {
        return abonado;
    }

    public void setAbonado(Abonado abonado) {
        this.abonado = abonado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getPeriodoAnio() {
        return periodoAnio;
    }

    public void setPeriodoAnio(Integer periodoAnio) {
        this.periodoAnio = periodoAnio;
    }

    public Integer getPeriodoMes() {
        return periodoMes;
    }

    public void setPeriodoMes(Integer periodoMes) {
        this.periodoMes = periodoMes;
    }

    public LocalDate getFechaLectura() {
        return fechaLectura;
    }

    public void setFechaLectura(LocalDate fechaLectura) {
        this.fechaLectura = fechaLectura;
    }

    public BigDecimal getLecturaAnterior() {
        return lecturaAnterior;
    }

    public void setLecturaAnterior(BigDecimal lecturaAnterior) {
        this.lecturaAnterior = lecturaAnterior;
    }

    public BigDecimal getLecturaActual() {
        return lecturaActual;
    }

    public void setLecturaActual(BigDecimal lecturaActual) {
        this.lecturaActual = lecturaActual;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Consumo calculado (no persistido como columna propia, se deriva).
     */
    public BigDecimal getConsumo() {
        if (lecturaActual == null || lecturaAnterior == null) {
            return BigDecimal.ZERO;
        }
        return lecturaActual.subtract(lecturaAnterior);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lectura)) {
            return false;
        }
        Lectura other = (Lectura) o;
        return idLectura != null && idLectura.equals(other.idLectura);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idLectura);
    }

    @Override
    public String toString() {
        return "Lectura{idLectura=" + idLectura + ", periodoAnio=" + periodoAnio
                + ", periodoMes=" + periodoMes + '}';
    }

}
