package com.asada.service;

import com.asada.domain.Lectura;
import com.asada.repository.LecturaRepository;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Genera reportes de consumo de agua a partir de las lecturas registradas,
 * con filtros por año, mes y abonado. Disponible para cualquier usuario
 * autenticado, sin importar su rol.
 */
@Service
public class ReporteService {

    private final LecturaRepository lecturaRepository;

    public ReporteService(LecturaRepository lecturaRepository) {
        this.lecturaRepository = lecturaRepository;
    }

    @Transactional(readOnly = true)
    public List<Lectura> buscar(Integer anio, Integer mes, Integer idAbonado) {
        return lecturaRepository.buscarParaReporte(anio, mes, idAbonado);
    }

    /**
     * Calcula el monto estimado a facturar por una lectura: tarifa base
     * más el consumo multiplicado por el precio del m³ de su categoría
     * tarifaria.
     */
    public BigDecimal calcularMontoEstimado(Lectura lectura) {
        var categoria = lectura.getAbonado().getCategoriaTarifa();
        return categoria.getTarifaBase().add(lectura.getConsumo().multiply(categoria.getPrecioM3()));
    }

    /**
     * Resumen del reporte: total de lecturas, consumo total y monto
     * estimado total.
     */
    public ResumenReporte calcularResumen(List<Lectura> lecturas) {
        BigDecimal consumoTotal = BigDecimal.ZERO;
        BigDecimal montoTotal = BigDecimal.ZERO;
        for (Lectura lectura : lecturas) {
            consumoTotal = consumoTotal.add(lectura.getConsumo());
            montoTotal = montoTotal.add(calcularMontoEstimado(lectura));
        }
        return new ResumenReporte(lecturas.size(), consumoTotal, montoTotal);
    }

    /**
     * Agrupa el consumo total por mes, para el reporte de "consumo
     * mensual" del período consultado.
     */
    public Map<Integer, BigDecimal> consumoPorMes(List<Lectura> lecturas) {
        Map<Integer, BigDecimal> resultado = new LinkedHashMap<>();
        for (int mes = 1; mes <= 12; mes++) {
            resultado.put(mes, BigDecimal.ZERO);
        }
        for (Lectura lectura : lecturas) {
            resultado.merge(lectura.getPeriodoMes(), lectura.getConsumo(), BigDecimal::add);
        }
        return resultado;
    }

    public record ResumenReporte(int totalLecturas, BigDecimal consumoTotal, BigDecimal montoEstimadoTotal) {

    }

}
