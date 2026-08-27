package com.asada.service;

import com.asada.domain.Ruta;
import com.asada.repository.RutaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Expone las rutas protegidas configuradas en base de datos, utilizadas
 * por {@link com.asada.security.SecurityConfig} para construir las reglas
 * de autorización de forma dinámica.
 */
@Service
public class RutaService {

    private final RutaRepository rutaRepository;

    public RutaService(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    @Transactional(readOnly = true)
    public List<Ruta> getRutas() {
        return rutaRepository.findAll();
    }

}
