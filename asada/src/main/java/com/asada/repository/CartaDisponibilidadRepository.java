package com.asada.repository;

import com.asada.domain.CartaDisponibilidad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartaDisponibilidadRepository extends JpaRepository<CartaDisponibilidad, Integer> {

    @Query("SELECT c FROM CartaDisponibilidad c LEFT JOIN FETCH c.usuario ORDER BY c.idCarta DESC")
    List<CartaDisponibilidad> findAllConDetalle();

}
