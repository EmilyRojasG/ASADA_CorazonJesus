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
import java.util.Objects;

/**
 * Abonado (persona física o jurídica) suscrita al servicio de agua potable.
 */
@Entity
@Table(name = "abonado")
public class Abonado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_abonado")
    private Integer idAbonado;

    @NotBlank
    @Column(name = "numero_abonado", nullable = false, unique = true, length = 20)
    private String numeroAbonado;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria_tarifa", nullable = false)
    private CategoriaTarifa categoriaTarifa;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String apellidos;

    @NotBlank
    @Column(nullable = false, unique = true, length = 25)
    private String cedula;

    @Column(length = 255)
    private String direccion;

    @Column(length = 25)
    private String telefono;

    @Column(length = 75)
    private String correo;

    @Column(name = "numero_medidor", length = 25)
    private String numeroMedidor;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "ruta_imagen", length = 1024)
    private String rutaImagen;

    private boolean activo;

    public Abonado() {
    }

    public Integer getIdAbonado() {
        return idAbonado;
    }

    public void setIdAbonado(Integer idAbonado) {
        this.idAbonado = idAbonado;
    }

    public String getNumeroAbonado() {
        return numeroAbonado;
    }

    public void setNumeroAbonado(String numeroAbonado) {
        this.numeroAbonado = numeroAbonado;
    }

    public CategoriaTarifa getCategoriaTarifa() {
        return categoriaTarifa;
    }

    public void setCategoriaTarifa(CategoriaTarifa categoriaTarifa) {
        this.categoriaTarifa = categoriaTarifa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNumeroMedidor() {
        return numeroMedidor;
    }

    public void setNumeroMedidor(String numeroMedidor) {
        this.numeroMedidor = numeroMedidor;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
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
        if (!(o instanceof Abonado)) {
            return false;
        }
        Abonado other = (Abonado) o;
        return idAbonado != null && idAbonado.equals(other.idAbonado);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idAbonado);
    }

    @Override
    public String toString() {
        return "Abonado{idAbonado=" + idAbonado + ", numeroAbonado=" + numeroAbonado + '}';
    }

}
