package com.asada.service;

import com.asada.domain.Abonado;
import com.asada.domain.ActividadFontanero;
import com.asada.domain.BitacoraActividad;
import com.asada.domain.BitacoraActividad.Accion;
import com.asada.domain.Usuario;
import com.asada.repository.ActividadFontaneroRepository;
import com.asada.repository.BitacoraActividadRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Administra las actividades registradas por el fontanero y deja, por cada
 * acción (registro, edición o eliminación), una entrada en la bitácora de
 * auditoría. Editar o eliminar una actividad requiere obligatoriamente un
 * motivo.
 */
@Service
public class ActividadFontaneroService {

    private final ActividadFontaneroRepository actividadRepository;
    private final BitacoraActividadRepository bitacoraRepository;

    public ActividadFontaneroService(ActividadFontaneroRepository actividadRepository,
            BitacoraActividadRepository bitacoraRepository) {
        this.actividadRepository = actividadRepository;
        this.bitacoraRepository = bitacoraRepository;
    }

    @Transactional(readOnly = true)
    public List<ActividadFontanero> getActividades() {
        return actividadRepository.findAllConDetalle();
    }

    @Transactional(readOnly = true)
    public Optional<ActividadFontanero> getActividad(Integer idActividad) {
        return actividadRepository.findById(idActividad);
    }

    @Transactional(readOnly = true)
    public List<BitacoraActividad> getBitacora() {
        return bitacoraRepository.findAllConDetalle();
    }

    @Transactional
    public void registrar(Usuario fontanero, Abonado abonado, String tipoActividad,
            String descripcion, LocalDate fechaActividad) {

        ActividadFontanero actividad = new ActividadFontanero();
        actividad.setUsuario(fontanero);
        actividad.setAbonado(abonado);
        actividad.setTipoActividad(tipoActividad);
        actividad.setDescripcion(descripcion);
        actividad.setFechaActividad(fechaActividad);
        actividad = actividadRepository.save(actividad);

        registrarBitacora(fontanero, Accion.REGISTRO, actividad, null);
    }

    @Transactional
    public void modificar(Usuario fontanero, Integer idActividad, Abonado abonado,
            String tipoActividad, String descripcion, LocalDate fechaActividad, String motivo) {

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el motivo de la edición.");
        }

        ActividadFontanero actividad = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new IllegalArgumentException("La actividad no fue encontrada."));

        actividad.setAbonado(abonado);
        actividad.setTipoActividad(tipoActividad);
        actividad.setDescripcion(descripcion);
        actividad.setFechaActividad(fechaActividad);
        actividad = actividadRepository.save(actividad);

        registrarBitacora(fontanero, Accion.EDICION, actividad, motivo);
    }

    @Transactional
    public void eliminar(Usuario fontanero, Integer idActividad, String motivo) {

        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el motivo de la eliminación.");
        }

        ActividadFontanero actividad = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new IllegalArgumentException("La actividad no fue encontrada."));

        // Se registra la bitácora ANTES de borrar, para conservar la
        // descripción de la actividad eliminada.
        registrarBitacora(fontanero, Accion.ELIMINACION, actividad, motivo);

        actividadRepository.delete(actividad);
    }

    private void registrarBitacora(Usuario usuario, Accion accion, ActividadFontanero actividad, String motivo) {
        BitacoraActividad entrada = new BitacoraActividad();
        entrada.setUsuario(usuario);
        entrada.setAccion(accion);
        entrada.setMotivo(motivo);

        String detalleAbonado = actividad.getAbonado() != null
                ? " (Abonado " + actividad.getAbonado().getNumeroAbonado() + ")"
                : "";
        entrada.setDescripcionActividad(actividad.getTipoActividad() + detalleAbonado
                + " - " + actividad.getDescripcion());

        bitacoraRepository.save(entrada);
    }

}
