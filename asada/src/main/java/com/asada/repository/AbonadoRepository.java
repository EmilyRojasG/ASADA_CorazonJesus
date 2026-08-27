package com.asada.repository;

import com.asada.domain.Abonado;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AbonadoRepository extends JpaRepository<Abonado, Integer> {

    List<Abonado> findByActivoTrue();

    Optional<Abonado> findByNumeroAbonado(String numeroAbonado);

    boolean existsByCedula(String cedula);

}
