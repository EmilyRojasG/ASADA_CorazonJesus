package com.asada.repository;

import com.asada.domain.CategoriaTarifa;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaTarifaRepository extends JpaRepository<CategoriaTarifa, Integer> {

    List<CategoriaTarifa> findByActivoTrue();

}
