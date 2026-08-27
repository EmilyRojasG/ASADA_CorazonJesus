package com.asada.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Bitácora de auditoría: registra cada acción (alta, edición o eliminación)
 * realizada sobre una {@link ActividadFontanero}, incluyendo el motivo
 * cuando se trata de una edición o eliminación.
 */
@Entity
@Table(name = "bitacora_actividad")
public class BitacoraActividad implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Accion {
        REGISTRO, EDICION, ELIMINACION
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bitacora")
    private Integer idBitacora;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Accion accion;

    @Column(name = "descripcion_actividad", nullable = false, length = 500)
    private String descripcionActividad;

    @Column(length = 500)
    private String motivo;

    @Column(name = "fecha_accion", insertable = false, updatable = false)
    private LocalDateTime fechaAccion;

    public BitacoraActividad() {
    }

    public Integer getIdBitacora() {
        return idBitacora;
    }

    public void setIdBitacora(Integer idBitacora) {
        this.idBitacora = idBitacora;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Accion getAccion() {
        return accion;
    }

    public void setAccion(Accion accion) {
        this.accion = accion;
    }

    public String getDescripcionActividad() {
        return descripcionActividad;
    }

    public void setDescripcionActividad(String descripcionActividad) {
        this.descripcionActividad = descripcionActividad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaAccion() {
        return fechaAccion;
    }

    /**
     * Frase legible para mostrar en la bitácora, por ejemplo:
     * "fontanero ha registrado una actividad".
     */
    public String getDescripcionAccion() {
        String verbo = switch (accion) {
            case REGISTRO -> "ha registrado una actividad";
            case EDICION -> "ha editado una actividad";
            case ELIMINACION -> "ha eliminado una actividad";
        };
        String nombreUsuario = usuario != null ? usuario.getUsername() : "un usuario";
        return nombreUsuario + " " + verbo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BitacoraActividad)) {
            return false;
        }
        BitacoraActividad other = (BitacoraActividad) o;
        return idBitacora != null && idBitacora.equals(other.idBitacora);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idBitacora);
    }

    @Override
    public String toString() {
        return "BitacoraActividad{idBitacora=" + idBitacora + ", accion=" + accion + '}';
    }

}
