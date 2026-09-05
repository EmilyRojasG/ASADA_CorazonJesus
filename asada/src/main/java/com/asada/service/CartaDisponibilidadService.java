package com.asada.service;

import com.asada.domain.CartaDisponibilidad;
import com.asada.domain.Usuario;
import com.asada.repository.CartaDisponibilidadRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Genera cartas de disponibilidad de agua para futuros nuevos abonados,
 * numerándolas de forma correlativa por año (CD-anio-consecutivo).
 */
@Service
public class CartaDisponibilidadService {

    private final CartaDisponibilidadRepository cartaDisponibilidadRepository;

    public CartaDisponibilidadService(CartaDisponibilidadRepository cartaDisponibilidadRepository) {
        this.cartaDisponibilidadRepository = cartaDisponibilidadRepository;
    }

    @Transactional(readOnly = true)
    public List<CartaDisponibilidad> getCartas() {
        return cartaDisponibilidadRepository.findAllConDetalle();
    }

    @Transactional(readOnly = true)
    public Optional<CartaDisponibilidad> getCarta(Integer idCarta) {
        return cartaDisponibilidadRepository.findById(idCarta);
    }

    @Transactional
    public CartaDisponibilidad emitir(Usuario usuario, String nombreSolicitante, String cedulaSolicitante,
            String direccionPropiedad, String numeroFinca, String planoCatastrado, String observaciones) {

        boolean sinFinca = numeroFinca == null || numeroFinca.isBlank();
        boolean sinPlano = planoCatastrado == null || planoCatastrado.isBlank();
        if (sinFinca && sinPlano) {
            throw new IllegalArgumentException(
                    "Debe indicar al menos el número de finca o el número de plano/presentación.");
        }

        CartaDisponibilidad carta = new CartaDisponibilidad();
        carta.setUsuario(usuario);
        carta.setNombreSolicitante(nombreSolicitante);
        carta.setCedulaSolicitante(cedulaSolicitante);
        carta.setDireccionPropiedad(direccionPropiedad);
        carta.setNumeroFinca(numeroFinca);
        carta.setPlanoCatastrado(planoCatastrado);
        carta.setObservaciones(observaciones);
        carta.setFechaEmision(LocalDate.now());
        carta.setNumeroCarta(generarNumeroCarta());

        return cartaDisponibilidadRepository.save(carta);
    }

    private String generarNumeroCarta() {
        int anio = LocalDate.now().getYear();
        long totalDelAnio = cartaDisponibilidadRepository.findAllConDetalle().stream()
                .filter(c -> c.getFechaEmision().getYear() == anio)
                .count();
        long consecutivo = totalDelAnio + 1;
        return String.format("CD-%d-%04d", anio, consecutivo);
    }

}
