package com.asada.repository;

import com.asada.domain.Lectura;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturaRepository extends JpaRepository<Lectura, Integer> {

    List<Lectura> findByAbonado_IdAbonadoOrderByPeriodoAnioDescPeriodoMesDesc(Integer idAbonado);

    List<Lectura> findByPeriodoAnioAndPeriodoMes(Integer periodoAnio, Integer periodoMes);

    Optional<Lectura> findByAbonado_IdAbonadoAndPeriodoAnioAndPeriodoMes(
            Integer idAbonado, Integer periodoAnio, Integer periodoMes);

    boolean existsByAbonado_IdAbonadoAndPeriodoAnioAndPeriodoMes(
            Integer idAbonado, Integer periodoAnio, Integer periodoMes);

    /**
     * Consulta flexible para el módulo de reportes: filtra por año
     * (obligatorio) y, opcionalmente, por mes y/o abonado.
     */
    @Query("SELECT l FROM Lectura l "
            + "LEFT JOIN FETCH l.abonado a "
            + "LEFT JOIN FETCH a.categoriaTarifa "
            + "LEFT JOIN FETCH l.usuario "
            + "WHERE l.periodoAnio = :anio "
            + "AND (:mes IS NULL OR l.periodoMes = :mes) "
            + "AND (:idAbonado IS NULL OR a.idAbonado = :idAbonado) "
            + "ORDER BY l.periodoMes ASC, a.numeroAbonado ASC")
    List<Lectura> buscarParaReporte(@Param("anio") Integer anio,
            @Param("mes") Integer mes,
            @Param("idAbonado") Integer idAbonado);

}
