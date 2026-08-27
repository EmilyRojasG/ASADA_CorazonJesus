package com.asada.service;

import com.asada.domain.CategoriaTarifa;
import com.asada.repository.CategoriaTarifaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaTarifaService {

    private final CategoriaTarifaRepository categoriaTarifaRepository;

    public CategoriaTarifaService(CategoriaTarifaRepository categoriaTarifaRepository) {
        this.categoriaTarifaRepository = categoriaTarifaRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaTarifa> getCategorias(boolean soloActivas) {
        if (soloActivas) {
            return categoriaTarifaRepository.findByActivoTrue();
        }
        return categoriaTarifaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<CategoriaTarifa> getCategoria(Integer idCategoriaTarifa) {
        return categoriaTarifaRepository.findById(idCategoriaTarifa);
    }

    @Transactional
    public void save(CategoriaTarifa categoriaTarifa) {
        try {
            categoriaTarifaRepository.save(categoriaTarifa);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(
                    "Ya existe una categoría tarifaria con esa descripción.", e);
        }
    }

    @Transactional
    public void delete(Integer idCategoriaTarifa) {
        if (!categoriaTarifaRepository.existsById(idCategoriaTarifa)) {
            throw new IllegalArgumentException(
                    "La categoría tarifaria con ID " + idCategoriaTarifa + " no existe.");
        }
        try {
            categoriaTarifaRepository.deleteById(idCategoriaTarifa);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException(
                    "No se puede eliminar la categoría: tiene abonados asociados. "
                    + "Puede desactivarla en su lugar.", e);
        }
    }

}
