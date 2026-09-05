package com.asada.service;

import com.asada.domain.ArchivoCarta;
import com.asada.domain.CartaDisponibilidad;
import com.asada.domain.Usuario;
import com.asada.repository.ArchivoCartaRepository;
import com.asada.repository.CartaDisponibilidadRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Administra los archivos adjuntos de una carta de disponibilidad
 * (documentos de respaldo del trámite: cédula, plano, etc.), que pueden
 * ser de cualquier tipo, no solo imágenes.
 */
@Service
public class ArchivoCartaService {

    private final ArchivoCartaRepository archivoCartaRepository;
    private final CartaDisponibilidadRepository cartaDisponibilidadRepository;
    private final ImageStorageService imageStorageService;

    public ArchivoCartaService(ArchivoCartaRepository archivoCartaRepository,
            CartaDisponibilidadRepository cartaDisponibilidadRepository,
            ImageStorageService imageStorageService) {
        this.archivoCartaRepository = archivoCartaRepository;
        this.cartaDisponibilidadRepository = cartaDisponibilidadRepository;
        this.imageStorageService = imageStorageService;
    }

    @Transactional(readOnly = true)
    public List<ArchivoCarta> getArchivos(Integer idCarta) {
        return archivoCartaRepository.findByCartaOrderByFechaSubidaDesc(idCarta);
    }

    /**
     * Trae todos los archivos de todas las cartas, agrupados por el id
     * de la carta a la que pertenecen, para mostrarlos en el listado sin
     * hacer una consulta por cada fila.
     */
    @Transactional(readOnly = true)
    public Map<Integer, List<ArchivoCarta>> getArchivosAgrupadosPorCarta() {
        return archivoCartaRepository.findAllConDetalle().stream()
                .collect(Collectors.groupingBy(a -> a.getCarta().getIdCarta()));
    }

    @Transactional
    public void subirArchivos(Integer idCarta, Usuario usuario, MultipartFile[] archivos) {

        CartaDisponibilidad carta = cartaDisponibilidadRepository.findById(idCarta)
                .orElseThrow(() -> new IllegalArgumentException("La carta no fue encontrada."));

        if (archivos == null || archivos.length == 0) {
            throw new IllegalArgumentException("Debe seleccionar al menos un archivo.");
        }

        for (MultipartFile archivo : archivos) {
            if (archivo == null || archivo.isEmpty()) {
                continue;
            }
            try {
                String ruta = imageStorageService.uploadFile(archivo, "carta_disponibilidad/" + idCarta);

                ArchivoCarta entidad = new ArchivoCarta();
                entidad.setCarta(carta);
                entidad.setUsuario(usuario);
                entidad.setNombreOriginal(archivo.getOriginalFilename());
                entidad.setRutaArchivo(ruta);
                entidad.setTipoContenido(archivo.getContentType());
                archivoCartaRepository.save(entidad);
            } catch (IOException e) {
                throw new IllegalStateException(
                        "No se pudo guardar el archivo '" + archivo.getOriginalFilename() + "'.", e);
            }
        }
    }

}
