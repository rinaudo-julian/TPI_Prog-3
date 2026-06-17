package com.utn.backend.controller;

import com.utn.backend.enums.Estado;
import com.utn.backend.enums.FormaPago;
import com.utn.backend.enums.Rol;
import com.utn.backend.model.Categoria;
import com.utn.backend.model.Pedido;
import com.utn.backend.model.Producto;
import com.utn.backend.model.Usuario;
import com.utn.backend.repository.CategoriaRepository;
import com.utn.backend.repository.PedidoRepository;
import com.utn.backend.repository.ProductoRepository;
import com.utn.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PedidoControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private PedidoRepository pedidoRepository;

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private CategoriaRepository categoriaRepository;

  @Autowired
  private ProductoRepository productoRepository;

  @BeforeEach
  void setUp() {
    pedidoRepository.deleteAll();
    productoRepository.deleteAll();
    usuarioRepository.deleteAll();
    categoriaRepository.deleteAll();
  }

  @Test
  @Transactional
  void createShouldPersistPedidoAndReduceProductStock() throws Exception {
    Categoria categoria = new Categoria();
    categoria.setNombre("Electronica");
    categoria.setDescripcion("Productos electronicos");
    categoria = categoriaRepository.save(categoria);

    Usuario usuario = new Usuario();
    usuario.setNombre("Juan");
    usuario.setApellido("Perez");
    usuario.setEmail("juan.perez@mail.com");
    usuario.setCelular("+5491122334455");
    usuario.setContrasena("hashed-user");
    usuario.setRol(Rol.USUARIO);
    usuario = usuarioRepository.save(usuario);

    Producto producto1 = new Producto();
    producto1.setNombre("Producto 1");
    producto1.setDescripcion("Descripcion 1");
    producto1.setPrecio(100.0);
    producto1.setStock(10);
    producto1.setImagen("producto-1.jpg");
    producto1.setDisponible(true);
    producto1.setCategoria(categoria);
    producto1 = productoRepository.save(producto1);

    Producto producto2 = new Producto();
    producto2.setNombre("Producto 2");
    producto2.setDescripcion("Descripcion 2");
    producto2.setPrecio(50.0);
    producto2.setStock(20);
    producto2.setImagen("producto-2.jpg");
    producto2.setDisponible(true);
    producto2.setCategoria(categoria);
    producto2 = productoRepository.save(producto2);

    mockMvc.perform(post("/pedidos")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "estado": "PENDIENTE",
              "formaPago": "TARJETA",
              "idUsuario": %d,
              "detallePedido": [
                {"idProducto": %d, "cantidad": 2},
                {"idProducto": %d, "cantidad": 3}
              ]
            }
            """.formatted(usuario.getId(), producto1.getId(), producto2.getId())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.fecha").exists())
        .andExpect(jsonPath("$.estado").value(Estado.PENDIENTE.name()))
        .andExpect(jsonPath("$.total").value(350.0))
        .andExpect(jsonPath("$.formaPago").value(FormaPago.TARJETA.name()))
        .andExpect(jsonPath("$.idUsuario").value(usuario.getId()))
        .andExpect(jsonPath("$.detalles", hasSize(2)))
        .andExpect(jsonPath("$.detalles[*].cantidad", hasItem(2)))
        .andExpect(jsonPath("$.detalles[*].cantidad", hasItem(3)))
        .andExpect(jsonPath("$.detalles[*].subtotal", hasItem(200.0)))
        .andExpect(jsonPath("$.detalles[*].subtotal", hasItem(150.0)))
        .andExpect(jsonPath("$.detalles[*].producto.id", hasItem(producto1.getId().intValue())))
        .andExpect(jsonPath("$.detalles[*].producto.nombre", hasItem("Producto 1")))
        .andExpect(jsonPath("$.detalles[*].producto.precio", hasItem(100.0)))
        .andExpect(jsonPath("$.detalles[*].producto.stock", hasItem(8)))
        .andExpect(jsonPath("$.detalles[*].producto.descripcion", hasItem("Descripcion 1")))
        .andExpect(jsonPath("$.detalles[*].producto.imagen", hasItem("producto-1.jpg")))
        .andExpect(jsonPath("$.detalles[*].producto.disponible", hasItem(true)))
        .andExpect(jsonPath("$.detalles[*].producto.categoria.id", hasItem(categoria.getId().intValue())))
        .andExpect(jsonPath("$.detalles[*].producto.categoria.nombre", hasItem("Electronica")))
        .andExpect(jsonPath("$.detalles[*].producto.categoria.descripcion", hasItem("Productos electronicos")))
        .andExpect(jsonPath("$.detalles[*].producto.id", hasItem(producto2.getId().intValue())))
        .andExpect(jsonPath("$.detalles[*].producto.nombre", hasItem("Producto 2")))
        .andExpect(jsonPath("$.detalles[*].producto.precio", hasItem(50.0)))
        .andExpect(jsonPath("$.detalles[*].producto.stock", hasItem(17)))
        .andExpect(jsonPath("$.detalles[*].producto.descripcion", hasItem("Descripcion 2")))
        .andExpect(jsonPath("$.detalles[*].producto.imagen", hasItem("producto-2.jpg")))
        .andExpect(jsonPath("$.detalles[*].producto.disponible", hasItem(true)));

    List<Pedido> pedidos = pedidoRepository.findAll();
    assertEquals(1, pedidos.size());

    Pedido pedido = pedidos.get(0);
    assertEquals(Estado.PENDIENTE, pedido.getEstado());
    assertEquals(FormaPago.TARJETA, pedido.getFormaPago());
    assertEquals(usuario.getId(), pedido.getUsuario().getId());
    assertEquals(350.0, pedido.getTotal());
    assertEquals(2, pedido.getDetallePedidos().size());

    Producto savedProducto1 = productoRepository.findById(producto1.getId()).orElseThrow();
    Producto savedProducto2 = productoRepository.findById(producto2.getId()).orElseThrow();

    assertEquals(8, savedProducto1.getStock());
    assertEquals(17, savedProducto2.getStock());
    assertTrue(savedProducto1.isDisponible());
    assertTrue(savedProducto2.isDisponible());
    assertFalse(savedProducto1.isEliminado());
    assertFalse(savedProducto2.isEliminado());
  }
}
