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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

  @Test
  @Transactional
  void findAllShouldReturnActivePedidosAndExcludeDeletedOnes() throws Exception {
    SeedData seed = seedPedidoData();

    createPedido(seed.usuario.getId(), seed.producto1.getId(), 2);
    createPedido(seed.usuario.getId(), seed.producto2.getId(), 3);
    createPedido(seed.usuario.getId(), seed.producto1.getId(), 1);

    Pedido pedidoEliminado = pedidoRepository.findAll().stream()
        .filter(pedido -> pedido.getTotal().equals(100.0))
        .findFirst()
        .orElseThrow();
    pedidoRepository.deleteById(pedidoEliminado.getId());

    mockMvc.perform(get("/pedidos"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id").exists())
        .andExpect(jsonPath("$[0].fecha").exists())
        .andExpect(jsonPath("$[0].estado").exists())
        .andExpect(jsonPath("$[0].total").exists())
        .andExpect(jsonPath("$[0].formaPago").exists())
        .andExpect(jsonPath("$[0].idUsuario").exists())
        .andExpect(jsonPath("$[0].detalles", hasSize(1)))
        .andExpect(jsonPath("$[0].detalles[0].id").exists())
        .andExpect(jsonPath("$[0].detalles[0].cantidad").exists())
        .andExpect(jsonPath("$[0].detalles[0].subtotal").exists())
        .andExpect(jsonPath("$[0].detalles[0].producto.id").exists())
        .andExpect(jsonPath("$[0].detalles[0].producto.nombre").exists())
        .andExpect(jsonPath("$[0].detalles[0].producto.precio").exists())
        .andExpect(jsonPath("$[0].detalles[0].producto.descripcion").exists())
        .andExpect(jsonPath("$[0].detalles[0].producto.stock").exists())
        .andExpect(jsonPath("$[0].detalles[0].producto.imagen").exists())
        .andExpect(jsonPath("$[0].detalles[0].producto.disponible").exists())
        .andExpect(jsonPath("$[0].detalles[0].producto.categoria.id").exists())
        .andExpect(jsonPath("$[0].detalles[0].producto.categoria.nombre").exists())
        .andExpect(jsonPath("$[0].detalles[0].producto.categoria.descripcion").exists());

    List<Pedido> pedidos = pedidoRepository.findAll();
    assertEquals(2, pedidos.size());
    assertTrue(pedidos.stream().noneMatch(pedido -> pedido.getTotal().equals(100.0)));
  }

  @Test
  void findAllShouldReturnEmptyArrayWhenNoPedidosExist() throws Exception {
    mockMvc.perform(get("/pedidos"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @Transactional
  void findByIdShouldReturnPedidoWhenItExists() throws Exception {
    SeedData seed = seedPedidoData();

    createPedido(seed.usuario.getId(), seed.producto1.getId(), 2);

    Pedido pedido = pedidoRepository.findAll().get(0);

    mockMvc.perform(get("/pedidos/{id}", pedido.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(pedido.getId()))
        .andExpect(jsonPath("$.fecha").exists())
        .andExpect(jsonPath("$.estado").value(Estado.PENDIENTE.name()))
        .andExpect(jsonPath("$.total").value(200.0))
        .andExpect(jsonPath("$.formaPago").value(FormaPago.TARJETA.name()))
        .andExpect(jsonPath("$.idUsuario").value(seed.usuario.getId()))
        .andExpect(jsonPath("$.detalles", hasSize(1)))
        .andExpect(jsonPath("$.detalles[0].id").exists())
        .andExpect(jsonPath("$.detalles[0].cantidad").value(2))
        .andExpect(jsonPath("$.detalles[0].subtotal").value(200.0))
        .andExpect(jsonPath("$.detalles[0].producto.id").value(seed.producto1.getId()))
        .andExpect(jsonPath("$.detalles[0].producto.nombre").value("Producto 1"))
        .andExpect(jsonPath("$.detalles[0].producto.precio").value(100.0))
        .andExpect(jsonPath("$.detalles[0].producto.descripcion").value("Descripcion 1"))
        .andExpect(jsonPath("$.detalles[0].producto.stock").value(8))
        .andExpect(jsonPath("$.detalles[0].producto.imagen").value("producto-1.jpg"))
        .andExpect(jsonPath("$.detalles[0].producto.disponible").value(true))
        .andExpect(jsonPath("$.detalles[0].producto.categoria.id").value(seed.producto1.getCategoria().getId()))
        .andExpect(jsonPath("$.detalles[0].producto.categoria.nombre").value("Electronica"))
        .andExpect(jsonPath("$.detalles[0].producto.categoria.descripcion").value("Productos electronicos"));
  }

  @Test
  void findByIdShouldReturn404WhenPedidoDoesNotExist() throws Exception {
    mockMvc.perform(get("/pedidos/{id}", 999L))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value("Recurso no encontrado"));
  }

  private void createPedido(Long usuarioId, Long productoId, int cantidad) throws Exception {
    mockMvc.perform(post("/pedidos")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "estado": "PENDIENTE",
              "formaPago": "TARJETA",
              "idUsuario": %d,
              "detallePedido": [
                {"idProducto": %d, "cantidad": %d}
              ]
            }
            """.formatted(usuarioId, productoId, cantidad)))
        .andExpect(status().isCreated());
  }

  private SeedData seedPedidoData() {
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

    return new SeedData(usuario, producto1, producto2);
  }

  private record SeedData(Usuario usuario, Producto producto1, Producto producto2) {
  }
}
