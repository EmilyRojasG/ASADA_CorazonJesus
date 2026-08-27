package com.asada.service;

import com.asada.repository.AbonadoRepository;
import com.asada.repository.CategoriaTarifaRepository;
import com.asada.repository.LecturaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Calcula los indicadores mostrados en el panel principal (dashboard):
 * total de abonados, total de categorías tarifarias, cantidad de lecturas
 * registradas y el consumo total de agua del mes en curso.
 */
@Service
public class DashboardService {

    private final AbonadoRepository abonadoRepository;
    private final CategoriaTarifaRepository categoriaTarifaRepository;
    private final LecturaRepository lecturaRepository;

    public DashboardService(AbonadoRepository abonadoRepository,
            CategoriaTarifaRepository categoriaTarifaRepository,
            LecturaRepository lecturaRepository) {
        this.abonadoRepository = abonadoRepository;
        this.categoriaTarifaRepository = categoriaTarifaRepository;
        this.lecturaRepository = lecturaRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResumen getResumen() {

        long totalAbonados = abonadoRepository.count();
        long totalCategorias = categoriaTarifaRepository.count();
        long totalLecturas = lecturaRepository.count();

        LocalDate hoy = LocalDate.now();
        var lecturasDelMes = lecturaRepository.findByPeriodoAnioAndPeriodoMes(
                hoy.getYear(), hoy.getMonthValue());

        BigDecimal consumoDelMes = lecturasDelMes.stream()
                .map(l -> l.getConsumo())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardResumen(totalAbonados, totalCategorias, totalLecturas,
                lecturasDelMes.size(), consumoDelMes);
    }

    /**
     * DTO simple con los indicadores del dashboard.
     */
    public record DashboardResumen(
            long totalAbonados,
            long totalCategorias,
            long totalLecturas,
            int lecturasDelMes,
            BigDecimal consumoDelMes) {

    }

}
