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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Carta de disponibilidad de agua: documento que la ASADA emite para
 * futuros nuevos abonados (por ejemplo, para trámites de permisos de
 * construcción), certificando que puede brindarles el servicio. No
 * requiere que la persona ya esté registrada como abonado.
 */
@Entity
@Table(name = "carta_disponibilidad")
public class CartaDisponibilidad implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carta")
    private Integer idCarta;

    @Column(name = "numero_carta", unique = true, nullable = false, length = 30)
    private String numeroCarta;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @NotBlank
    @Column(name = "nombre_solicitante", nullable = false, length = 120)
    private String nombreSolicitante;

    @NotBlank
    @Column(name = "cedula_solicitante", nullable = false, length = 25)
    private String cedulaSolicitante;

    @NotBlank
    @Column(name = "direccion_propiedad", nullable = false, length = 255)
    private String direccionPropiedad;

    @Column(name = "numero_finca", length = 25)
    private String numeroFinca;

    @Column(name = "plano_catastrado", length = 60)
    private String planoCatastrado;

    @NotNull
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(length = 500)
    private String observaciones;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public CartaDisponibilidad() {
    }

    public Integer getIdCarta() {
        return idCarta;
    }

    public void setIdCarta(Integer idCarta) {
        this.idCarta = idCarta;
    }

    public String getNumeroCarta() {
        return numeroCarta;
    }

    public void setNumeroCarta(String numeroCarta) {
        this.numeroCarta = numeroCarta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombreSolicitante() {
        return nombreSolicitante;
    }

    public void setNombreSolicitante(String nombreSolicitante) {
        this.nombreSolicitante = nombreSolicitante;
    }

    public String getCedulaSolicitante() {
        return cedulaSolicitante;
    }

    public void setCedulaSolicitante(String cedulaSolicitante) {
        this.cedulaSolicitante = cedulaSolicitante;
    }

    public String getDireccionPropiedad() {
        return direccionPropiedad;
    }

    public void setDireccionPropiedad(String direccionPropiedad) {
        this.direccionPropiedad = direccionPropiedad;
    }

    public String getNumeroFinca() {
        return numeroFinca;
    }

    public void setNumeroFinca(String numeroFinca) {
        this.numeroFinca = numeroFinca;
    }

    public String getPlanoCatastrado() {
        return planoCatastrado;
    }

    public void setPlanoCatastrado(String planoCatastrado) {
        this.planoCatastrado = planoCatastrado;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CartaDisponibilidad)) {
            return false;
        }
        CartaDisponibilidad other = (CartaDisponibilidad) o;
        return idCarta != null && idCarta.equals(other.idCarta);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idCarta);
    }

    @Override
    public String toString() {
        return "CartaDisponibilidad{idCarta=" + idCarta + ", numeroCarta=" + numeroCarta + '}';
    }

}
