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
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Archivo adjunto a una carta de disponibilidad de agua (por ejemplo,
 * el plano catastrado escaneado, cédula del solicitante, o cualquier
 * otro documento de respaldo del trámite). Se puede adjuntar cualquier
 * tipo de archivo, no solo imágenes.
 */
@Entity
@Table(name = "carta_archivo")
public class ArchivoCarta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_archivo")
    private Integer idArchivo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_carta", nullable = false)
    private CartaDisponibilidad carta;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @NotBlank
    @Column(name = "nombre_original", nullable = false, length = 255)
    private String nombreOriginal;

    @NotBlank
    @Column(name = "ruta_archivo", nullable = false, length = 1024)
    private String rutaArchivo;

    @Column(name = "tipo_contenido", length = 120)
    private String tipoContenido;

    @Column(name = "fecha_subida", insertable = false, updatable = false)
    private LocalDateTime fechaSubida;

    public ArchivoCarta() {
    }

    public Integer getIdArchivo() {
        return idArchivo;
    }

    public void setIdArchivo(Integer idArchivo) {
        this.idArchivo = idArchivo;
    }

    public CartaDisponibilidad getCarta() {
        return carta;
    }

    public void setCarta(CartaDisponibilidad carta) {
        this.carta = carta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public void setNombreOriginal(String nombreOriginal) {
        this.nombreOriginal = nombreOriginal;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public void setTipoContenido(String tipoContenido) {
        this.tipoContenido = tipoContenido;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArchivoCarta)) {
            return false;
        }
        ArchivoCarta other = (ArchivoCarta) o;
        return idArchivo != null && idArchivo.equals(other.idArchivo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idArchivo);
    }

    @Override
    public String toString() {
        return "ArchivoCarta{idArchivo=" + idArchivo + ", nombreOriginal=" + nombreOriginal + '}';
    }

}
