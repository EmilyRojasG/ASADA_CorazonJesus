package com.asada.service;

import com.asada.domain.Abonado;
import com.asada.domain.Lectura;
import com.asada.domain.Usuario;
import com.asada.repository.LecturaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LecturaService {

    private final LecturaRepository lecturaRepository;

    public LecturaService(LecturaRepository lecturaRepository) {
        this.lecturaRepository = lecturaRepository;
    }

    @Transactional(readOnly = true)
    public List<Lectura> getLecturas() {
        return lecturaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Lectura> getHistorial(Integer idAbonado) {
        return lecturaRepository.findByAbonado_IdAbonadoOrderByPeriodoAnioDescPeriodoMesDesc(idAbonado);
    }

    /**
     * Devuelve la última lectura registrada de un abonado (si existe), para
     * precargar el valor de "lectura anterior" al registrar una nueva.
     */
    @Transactional(readOnly = true)
    public Optional<Lectura> getUltimaLectura(Integer idAbonado) {
        return getHistorial(idAbonado).stream().findFirst();
    }

    @Transactional(readOnly = true)
    public Optional<Lectura> getLectura(Integer idLectura) {
        return lecturaRepository.findById(idLectura);
    }

    /**
     * Registra una nueva lectura, calculando automáticamente la lectura
     * anterior a partir del historial del abonado, validando que no exista
     * ya una lectura para el mismo abonado/año/mes y que la lectura actual
     * no sea menor a la anterior.
     */
    @Transactional
    public Lectura registrar(Abonado abonado, Usuario usuario, Integer periodoAnio, Integer periodoMes,
            LocalDate fechaLectura, BigDecimal lecturaActual, String observaciones) {

        if (lecturaRepository.existsByAbonado_IdAbonadoAndPeriodoAnioAndPeriodoMes(
                abonado.getIdAbonado(), periodoAnio, periodoMes)) {
            throw new IllegalArgumentException(
                    "Ya existe una lectura registrada para este abonado en ese período (año/mes).");
        }

        BigDecimal lecturaAnterior = getUltimaLectura(abonado.getIdAbonado())
                .map(Lectura::getLecturaActual)
                .orElse(BigDecimal.ZERO);

        if (lecturaActual.compareTo(lecturaAnterior) < 0) {
            throw new IllegalArgumentException(
                    "La lectura actual (" + lecturaActual + ") no puede ser menor a la lectura anterior ("
                    + lecturaAnterior + ").");
        }

        Lectura lectura = new Lectura();
        lectura.setAbonado(abonado);
        lectura.setUsuario(usuario);
        lectura.setPeriodoAnio(periodoAnio);
        lectura.setPeriodoMes(periodoMes);
        lectura.setFechaLectura(fechaLectura);
        lectura.setLecturaAnterior(lecturaAnterior);
        lectura.setLecturaActual(lecturaActual);
        lectura.setObservaciones(observaciones);

        try {
            return lecturaRepository.save(lectura);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(
                    "Ya existe una lectura registrada para este abonado en ese período (año/mes).", e);
        }
    }

    @Transactional
    public void delete(Integer idLectura) {
        if (!lecturaRepository.existsById(idLectura)) {
            throw new IllegalArgumentException("La lectura con ID " + idLectura + " no existe.");
        }
        lecturaRepository.deleteById(idLectura);
    }

}
