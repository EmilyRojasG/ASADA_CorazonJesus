package com.asada.repository;

import com.asada.domain.ArchivoCarta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchivoCartaRepository extends JpaRepository<ArchivoCarta, Integer> {

    @Query("SELECT a FROM ArchivoCarta a LEFT JOIN FETCH a.usuario "
            + "WHERE a.carta.idCarta = :idCarta ORDER BY a.fechaSubida DESC")
    List<ArchivoCarta> findByCartaOrderByFechaSubidaDesc(@Param("idCarta") Integer idCarta);

    /**
     * Trae, en una sola consulta, todos los archivos de todas las cartas
     * (con su usuario ya cargado), para poder agruparlos por carta al
     * mostrar el listado sin hacer una consulta por fila.
     */
    @Query("SELECT a FROM ArchivoCarta a LEFT JOIN FETCH a.usuario ORDER BY a.fechaSubida DESC")
    List<ArchivoCarta> findAllConDetalle();

}
