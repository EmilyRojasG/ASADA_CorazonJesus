package com.asada.repository;

import com.asada.domain.BitacoraActividad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BitacoraActividadRepository extends JpaRepository<BitacoraActividad, Integer> {

    @Query("SELECT b FROM BitacoraActividad b "
            + "LEFT JOIN FETCH b.usuario "
            + "ORDER BY b.fechaAccion DESC, b.idBitacora DESC")
    List<BitacoraActividad> findAllConDetalle();

}
