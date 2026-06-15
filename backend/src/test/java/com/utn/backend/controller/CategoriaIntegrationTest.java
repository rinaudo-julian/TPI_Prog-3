package com.utn.backend.controller;

import com.utn.backend.model.Categoria;
import com.utn.backend.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoriaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    void setUp() {
        categoriaRepository.deleteAll();
    }

    @Test
    void findAllShouldReturnOnlyActiveCategories() throws Exception {
        Categoria categoria1 = createCategoria("Electronica", "Productos electronicos", false);
        createCategoria("Hogar", "Productos para el hogar", false);
        createCategoria("Juguetes", "Categoria eliminada", true);

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[*].nombre", hasItem(categoria1.getNombre())))
                .andExpect(jsonPath("$[*].nombre", not(hasItem("Juguetes"))));
    }

    @Test
    void findByIdShouldReturnCategoryWhenItExists() throws Exception {
        Categoria categoria = createCategoria("Electronica", "Productos electronicos", false);

        mockMvc.perform(get("/categorias/{id}", categoria.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoria.getId()))
                .andExpect(jsonPath("$.nombre").value("Electronica"))
                .andExpect(jsonPath("$.descripcion").value("Productos electronicos"));
    }

    @Test
    void findByIdShouldReturn404WhenCategoryDoesNotExist() throws Exception {
        mockMvc.perform(get("/categorias/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Recurso no encontrado"));
    }

    @Test
    void updateShouldModifyCategoryCorrectly() throws Exception {
        Categoria categoria = createCategoria("Electronica", "Productos electronicos", false);

        mockMvc.perform(put("/categorias/{id}", categoria.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Hogar",
                                  "descripcion": "Productos para el hogar"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoria.getId()))
                .andExpect(jsonPath("$.nombre").value("Hogar"))
                .andExpect(jsonPath("$.descripcion").value("Productos para el hogar"));

        Categoria updatedCategoria = categoriaRepository.findById(categoria.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals("Hogar", updatedCategoria.getNombre());
        org.junit.jupiter.api.Assertions.assertEquals("Productos para el hogar", updatedCategoria.getDescripcion());
    }

    @Test
    void updateShouldReturn404WhenCategoryDoesNotExist() throws Exception {
        mockMvc.perform(put("/categorias/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Hogar",
                                  "descripcion": "Productos para el hogar"
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Recurso no encontrado"));
    }

    @Test
    void updateShouldReturn409WhenNameAlreadyExists() throws Exception {
        Categoria categoria1 = createCategoria("Electronica", "Productos electronicos", false);
        createCategoria("Hogar", "Productos para el hogar", false);

        mockMvc.perform(put("/categorias/{id}", categoria1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Hogar",
                                  "descripcion": "Descripcion nueva"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Ya existe una categoría con ese nombre"));
    }

    @Test
    void deleteShouldSoftDeleteCategory() throws Exception {
        Categoria categoria = createCategoria("Electronica", "Productos electronicos", false);

        mockMvc.perform(delete("/categorias/{id}", categoria.getId()))
                .andExpect(status().isNoContent());

        Categoria deletedCategoria = categoriaRepository.findById(categoria.getId()).orElseThrow();
        List<Categoria> activeCategories = categoriaRepository.findAll();

        org.junit.jupiter.api.Assertions.assertTrue(deletedCategoria.isEliminado());
        org.junit.jupiter.api.Assertions.assertEquals(0, activeCategories.size());
    }

    @Test
    void deleteShouldReturn404WhenCategoryDoesNotExist() throws Exception {
        mockMvc.perform(delete("/categorias/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Recurso no encontrado"));
    }

    @Test
    void createShouldPersistCategoryInDatabase() throws Exception {
        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Electronica",
                                  "descripcion": "Productos electronicos"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Electronica"))
                .andExpect(jsonPath("$.descripcion").value("Productos electronicos"));

        Categoria categoria = categoriaRepository.findAll().stream()
                .filter(item -> "Electronica".equals(item.getNombre()))
                .findFirst()
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals("Electronica", categoria.getNombre());
        org.junit.jupiter.api.Assertions.assertEquals("Productos electronicos", categoria.getDescripcion());
    }

    @Test
    void createShouldReturn409WhenNameAlreadyExists() throws Exception {
        createCategoria("Electronica", "Productos electronicos", false);

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Electronica",
                                  "descripcion": "Otra descripcion"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Ya existe una categoría con ese nombre"));
    }

    private Categoria createCategoria(String nombre, String descripcion, boolean eliminado) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoria.setEliminado(eliminado);
        return categoriaRepository.save(categoria);
    }
}
