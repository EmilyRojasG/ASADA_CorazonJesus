package com.asada.repository;

import com.asada.domain.Constante;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConstanteRepository extends JpaRepository<Constante, Integer> {

    Optional<Constante> findByAtributo(String atributo);

}
