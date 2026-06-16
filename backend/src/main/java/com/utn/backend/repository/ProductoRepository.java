package com.utn.backend.repository;

import com.utn.backend.model.Producto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends BaseRepository<Producto> {
    boolean existsByNombre(String nombre);

    boolean existsByNombreAndIdNot(String nombre, Long id);

    List<Producto> findAllByCategoriaIdAndEliminadoFalse(Long categoriaId);
}
