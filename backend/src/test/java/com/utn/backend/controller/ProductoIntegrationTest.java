package com.utn.backend.controller;

import com.utn.backend.model.Categoria;
import com.utn.backend.model.Producto;
import com.utn.backend.repository.CategoriaRepository;
import com.utn.backend.repository.ProductoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();
        categoriaRepository.deleteAll();
    }

    @Test
    void createShouldPersistProductAndReturnFullStructure() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNombre("Electronica");
        categoria.setDescripcion("Productos electronicos");
        categoria = categoriaRepository.save(categoria);

        mockMvc.perform(post("/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nombre": "Laptop Gaming Pro",
                          "precio": 1599.99,
                          "descripcion": "Laptop de alto rendimiento",
                          "stock": 25,
                          "imagen": "laptop.jpg",
                          "disponible": true,
                          "idCategoria": %d
                        }
                        """.formatted(categoria.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Laptop Gaming Pro"))
                .andExpect(jsonPath("$.precio").value(1599.99))
                .andExpect(jsonPath("$.descripcion").value("Laptop de alto rendimiento"))
                .andExpect(jsonPath("$.stock").value(25))
                .andExpect(jsonPath("$.imagen").value("laptop.jpg"))
                .andExpect(jsonPath("$.disponible").value(true))
                .andExpect(jsonPath("$.categoria.id").value(categoria.getId()))
                .andExpect(jsonPath("$.categoria.nombre").value("Electronica"))
                .andExpect(jsonPath("$.categoria.descripcion").value("Productos electronicos"));

        List<Producto> productos = productoRepository.findAll();
        Assertions.assertEquals(1, productos.size());

        Producto producto = productos.get(0);
        Assertions.assertEquals("Laptop Gaming Pro", producto.getNombre());
        Assertions.assertEquals(1599.99, producto.getPrecio(), 0.0001);
        Assertions.assertEquals("Laptop de alto rendimiento", producto.getDescripcion());
        Assertions.assertEquals(25, producto.getStock());
        Assertions.assertEquals("laptop.jpg", producto.getImagen());
        Assertions.assertTrue(producto.isDisponible());
        Assertions.assertEquals(categoria.getId(), producto.getCategoria().getId());
    }

    @Test
    void createShouldReturn404WhenCategoryDoesNotExist() throws Exception {
        mockMvc.perform(post("/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nombre": "Laptop Gaming Pro",
                          "precio": 1599.99,
                          "descripcion": "Laptop de alto rendimiento",
                          "stock": 25,
                          "imagen": "laptop.jpg",
                          "disponible": true,
                          "idCategoria": 999
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("La categoría no existe"));
    }

    @Test
    void findAllShouldReturnOnlyActiveProductsWithCategory() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNombre("Electronica");
        categoria.setDescripcion("Productos electronicos");
        categoria = categoriaRepository.save(categoria);

        Producto activo = new Producto();
        activo.setNombre("Laptop Gaming Pro");
        activo.setDescripcion("Laptop de alto rendimiento");
        activo.setPrecio(1599.99);
        activo.setStock(25);
        activo.setImagen("laptop.jpg");
        activo.setDisponible(true);
        activo.setCategoria(categoria);
        productoRepository.save(activo);

        Producto eliminado = new Producto();
        eliminado.setNombre("Laptop Vieja");
        eliminado.setDescripcion("No debe aparecer");
        eliminado.setPrecio(999.99);
        eliminado.setStock(3);
        eliminado.setImagen("vieja.jpg");
        eliminado.setDisponible(true);
        eliminado.setCategoria(categoria);
        eliminado.setEliminado(true);
        productoRepository.save(eliminado);

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(1)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].nombre").value("Laptop Gaming Pro"))
                .andExpect(jsonPath("$[0].precio").value(1599.99))
                .andExpect(jsonPath("$[0].descripcion").value("Laptop de alto rendimiento"))
                .andExpect(jsonPath("$[0].stock").value(25))
                .andExpect(jsonPath("$[0].imagen").value("laptop.jpg"))
                .andExpect(jsonPath("$[0].disponible").value(true))
                .andExpect(jsonPath("$[0].categoria.id").value(categoria.getId()))
                .andExpect(jsonPath("$[0].categoria.nombre").value("Electronica"))
                .andExpect(jsonPath("$[0].categoria.descripcion").value("Productos electronicos"))
                .andExpect(jsonPath("$[*].nombre", not(hasItem("Laptop Vieja"))));
    }

    @Test
    void findAllShouldReturnEmptyArrayWhenThereAreNoActiveProducts() throws Exception {
        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(0)));
    }
}
