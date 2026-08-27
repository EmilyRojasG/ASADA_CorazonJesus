package com.asada.repository;

import com.asada.domain.Lectura;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturaRepository extends JpaRepository<Lectura, Integer> {

    List<Lectura> findByAbonado_IdAbonadoOrderByPeriodoAnioDescPeriodoMesDesc(Integer idAbonado);

    List<Lectura> findByPeriodoAnioAndPeriodoMes(Integer periodoAnio, Integer periodoMes);

    Optional<Lectura> findByAbonado_IdAbonadoAndPeriodoAnioAndPeriodoMes(
            Integer idAbonado, Integer periodoAnio, Integer periodoMes);

    boolean existsByAbonado_IdAbonadoAndPeriodoAnioAndPeriodoMes(
            Integer idAbonado, Integer periodoAnio, Integer periodoMes);

}
