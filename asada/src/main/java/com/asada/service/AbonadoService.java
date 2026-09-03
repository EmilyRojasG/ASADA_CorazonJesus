package com.asada.service;

import com.asada.domain.Abonado;
import com.asada.repository.AbonadoRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AbonadoService {

    private final AbonadoRepository abonadoRepository;
    private final ImageStorageService imageStorageService;

    public AbonadoService(AbonadoRepository abonadoRepository, ImageStorageService imageStorageService) {
        this.abonadoRepository = abonadoRepository;
        this.imageStorageService = imageStorageService;
    }

    @Transactional(readOnly = true)
    public List<Abonado> getAbonados(boolean soloActivos) {
        if (soloActivos) {
            return abonadoRepository.findByActivoTrue();
        }
        return abonadoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Abonado> getAbonado(Integer idAbonado) {
        return abonadoRepository.findById(idAbonado);
    }

    @Transactional
    public void save(Abonado abonado, MultipartFile imagenFile) {

        final Integer idAbonadoActual = abonado.getIdAbonado();

        // El campo oculto "rutaImagen" del formulario envía una cadena
        // vacía (no null) cuando el abonado todavía no tiene foto; se
        // normaliza aquí para que la base de datos siempre guarde NULL
        // en ese caso, no "".
        if (abonado.getRutaImagen() != null && abonado.getRutaImagen().isBlank()) {
            abonado.setRutaImagen(null);
        }

        // Valida duplicados de cédula y número de abonado antes de guardar
        abonadoRepository.findByNumeroAbonado(abonado.getNumeroAbonado()).ifPresent(existente -> {
            if (idAbonadoActual == null || !existente.getIdAbonado().equals(idAbonadoActual)) {
                throw new IllegalArgumentException("Ya existe un abonado con ese número de abonado.");
            }
        });

        Abonado guardado;
        try {
            guardado = abonadoRepository.save(abonado);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(
                    "Ya existe un abonado con esa cédula o número de abonado.", e);
        }

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String rutaImagen = imageStorageService.uploadImage(imagenFile, "abonado", guardado.getIdAbonado());
                guardado.setRutaImagen(rutaImagen);
                abonadoRepository.save(guardado);
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo guardar la imagen del abonado.", e);
            }
        }
    }

    @Transactional
    public void delete(Integer idAbonado) {
        if (!abonadoRepository.existsById(idAbonado)) {
            throw new IllegalArgumentException("El abonado con ID " + idAbonado + " no existe.");
        }
        try {
            abonadoRepository.deleteById(idAbonado);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar el abonado: tiene lecturas registradas. "
                    + "Puede desactivarlo en su lugar.", e);
        }
    }

}
