package com.asada.repository;

import com.asada.domain.ActividadFontanero;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ActividadFontaneroRepository extends JpaRepository<ActividadFontanero, Integer> {

    @Query("SELECT a FROM ActividadFontanero a "
            + "LEFT JOIN FETCH a.usuario "
            + "LEFT JOIN FETCH a.abonado "
            + "ORDER BY a.fechaActividad DESC, a.idActividad DESC")
    List<ActividadFontanero> findAllConDetalle();

}
