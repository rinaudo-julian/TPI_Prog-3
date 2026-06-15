package com.utn.backend.repository;

import com.utn.backend.exception.ResourceNotFoundException;
import com.utn.backend.model.Categoria;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BaseRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    void deleteByIdShouldMarkEntityAsDeleted() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Electronica");
        categoria.setDescripcion("Productos electronicos");

        categoria = categoriaRepository.save(categoria);

        categoriaRepository.deleteById(categoria.getId());

        Categoria deletedCategoria = categoriaRepository.findById(categoria.getId()).orElseThrow();

        assertTrue(deletedCategoria.isEliminado());
        assertEquals(0, categoriaRepository.findAll().size());
    }

    @Test
    void deleteByIdShouldThrowWhenEntityDoesNotExist() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> categoriaRepository.deleteById(999L));

        assertEquals("Recurso no encontrado", exception.getMessage());
    }
}
