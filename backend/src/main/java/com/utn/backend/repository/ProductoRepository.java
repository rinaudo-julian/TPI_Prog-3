package com.utn.backend.repository;

import com.utn.backend.model.Producto;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends BaseRepository<Producto> {
    boolean existsByNombre(String nombre);
}
