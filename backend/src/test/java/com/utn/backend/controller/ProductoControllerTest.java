package com.utn.backend.controller;

import com.utn.backend.exception.GlobalHandlerException;
import com.utn.backend.service.impl.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
@Import(GlobalHandlerException.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @Test
    void createShouldReturn400WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "",
                                  "precio": 1599.99,
                                  "descripcion": "Laptop de alto rendimiento",
                                  "stock": 25,
                                  "imagen": "laptop.jpg",
                                  "disponible": true,
                                  "idCategoria": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("nombre: El nombre es obligatorio")));
    }

    @Test
    void createShouldReturn400WhenNameIsTooShort() throws Exception {
        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "a",
                                  "precio": 1599.99,
                                  "descripcion": "Laptop de alto rendimiento",
                                  "stock": 25,
                                  "imagen": "laptop.jpg",
                                  "disponible": true,
                                  "idCategoria": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("nombre: El nombre debe tener entre 2 y 100 caracteres")));
    }

    @Test
    void createShouldReturn400WhenNameIsTooLong() throws Exception {
        String longName = "a".repeat(101);

        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "%s",
                                  "precio": 1599.99,
                                  "descripcion": "Laptop de alto rendimiento",
                                  "stock": 25,
                                  "imagen": "laptop.jpg",
                                  "disponible": true,
                                  "idCategoria": 1
                                }
                                """.formatted(longName)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("nombre: El nombre debe tener entre 2 y 100 caracteres")));
    }

    @Test
    void createShouldReturn400WhenPriceIsMissing() throws Exception {
        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Laptop Gaming Pro",
                                  "descripcion": "Laptop de alto rendimiento",
                                  "stock": 25,
                                  "imagen": "laptop.jpg",
                                  "disponible": true,
                                  "idCategoria": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("precio: El precio es obligatorio")));
    }

    @Test
    void createShouldReturn400WhenPriceIsZero() throws Exception {
        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Laptop Gaming Pro",
                                  "precio": 0,
                                  "descripcion": "Laptop de alto rendimiento",
                                  "stock": 25,
                                  "imagen": "laptop.jpg",
                                  "disponible": true,
                                  "idCategoria": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("precio: El precio debe ser mayor a 0")));
    }

    @Test
    void createShouldReturn400WhenPriceIsNegative() throws Exception {
        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Laptop Gaming Pro",
                                  "precio": -1,
                                  "descripcion": "Laptop de alto rendimiento",
                                  "stock": 25,
                                  "imagen": "laptop.jpg",
                                  "disponible": true,
                                  "idCategoria": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("precio: El precio debe ser mayor a 0")));
    }

    @Test
    void createShouldReturn400WhenStockIsMissing() throws Exception {
        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Laptop Gaming Pro",
                                  "precio": 1599.99,
                                  "descripcion": "Laptop de alto rendimiento",
                                  "imagen": "laptop.jpg",
                                  "disponible": true,
                                  "idCategoria": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("stock: El stock es obligatorio")));
    }

    @Test
    void createShouldReturn400WhenStockIsNegative() throws Exception {
        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Laptop Gaming Pro",
                                  "precio": 1599.99,
                                  "descripcion": "Laptop de alto rendimiento",
                                  "stock": -1,
                                  "imagen": "laptop.jpg",
                                  "disponible": true,
                                  "idCategoria": 1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("stock: El stock no puede ser negativo")));
    }

    @Test
    void createShouldReturn400WhenCategoryIdIsMissing() throws Exception {
        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Laptop Gaming Pro",
                                  "precio": 1599.99,
                                  "descripcion": "Laptop de alto rendimiento",
                                  "stock": 25,
                                  "imagen": "laptop.jpg",
                                  "disponible": true
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("idCategoria: La categoría es obligatoria")));
    }

    @Test
    void createShouldReturn400WhenDescriptionIsTooLong() throws Exception {
        String longDescription = "a".repeat(501);

        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Laptop Gaming Pro",
                                  "precio": 1599.99,
                                  "descripcion": "%s",
                                  "stock": 25,
                                  "imagen": "laptop.jpg",
                                  "disponible": true,
                                  "idCategoria": 1
                                }
                                """.formatted(longDescription)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("descripcion: La descripción no puede exceder 500 caracteres")));
    }

    @Test
    void updateShouldReturn400WhenNameHasOneCharacter() throws Exception {
        mockMvc.perform(put("/productos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "a"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("nombre: El nombre debe tener entre 2 y 100 caracteres")));
    }

    @Test
    void updateShouldReturn400WhenNameIsTooLong() throws Exception {
        String longName = "a".repeat(101);

        mockMvc.perform(put("/productos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "%s"
                                }
                                """.formatted(longName)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("nombre: El nombre debe tener entre 2 y 100 caracteres")));
    }

    @Test
    void updateShouldReturn400WhenPriceIsZero() throws Exception {
        mockMvc.perform(put("/productos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "precio": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("precio: El precio debe ser mayor a 0")));
    }

    @Test
    void updateShouldReturn400WhenPriceIsNegative() throws Exception {
        mockMvc.perform(put("/productos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "precio": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("precio: El precio debe ser mayor a 0")));
    }

    @Test
    void updateShouldReturn400WhenStockIsNegative() throws Exception {
        mockMvc.perform(put("/productos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "stock": -1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("stock: El stock no puede ser negativo")));
    }

    @Test
    void updateShouldReturn400WhenDescriptionIsTooLong() throws Exception {
        String longDescription = "a".repeat(501);

        mockMvc.perform(put("/productos/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "descripcion": "%s"
                                }
                                """.formatted(longDescription)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.errors").value(hasItem("descripcion: La descripción no puede exceder 500 caracteres")));
    }
}
