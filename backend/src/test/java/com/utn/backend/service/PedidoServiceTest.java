package com.utn.backend.service;

import com.utn.backend.dto.DetallePedidoCreateRequestDTO;
import com.utn.backend.dto.DetallePedidoResponseDTO;
import com.utn.backend.dto.PedidoCreateRequestDTO;
import com.utn.backend.dto.PedidoResponseDTO;
import com.utn.backend.dto.ProductoResponseDTO;
import com.utn.backend.enums.Estado;
import com.utn.backend.enums.FormaPago;
import com.utn.backend.exception.BusinessException;
import com.utn.backend.exception.ResourceNotFoundException;
import com.utn.backend.mappers.PedidoMapper;
import com.utn.backend.model.Pedido;
import com.utn.backend.model.Producto;
import com.utn.backend.model.Usuario;
import com.utn.backend.repository.PedidoRepository;
import com.utn.backend.repository.ProductoRepository;
import com.utn.backend.repository.UsuarioRepository;
import com.utn.backend.service.impl.PedidoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void saveShouldCreateOrderSuccessfully() {
        Usuario usuario = createUsuario(1L);
        Producto producto1 = createProducto(1L, "Producto 1", 100.0, 10, true);
        Producto producto2 = createProducto(2L, "Producto 2", 50.0, 20, true);

        PedidoCreateRequestDTO request = createRequest(1L,
                createDetalleRequest(1L, 2),
                createDetalleRequest(2L, 3));

        when(usuarioRepository.findByIdAndEliminadoFalse(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findAllById(anyCollection())).thenReturn(List.of(producto1, producto2));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(99L);
            pedido.getDetallePedidos().forEach(detalle -> {
                if (detalle.getProducto() != null && Long.valueOf(1L).equals(detalle.getProducto().getId())) {
                    detalle.setId(201L);
                }
                if (detalle.getProducto() != null && Long.valueOf(2L).equals(detalle.getProducto().getId())) {
                    detalle.setId(202L);
                }
            });
            return pedido;
        });
        when(pedidoMapper.toDto(any(Pedido.class))).thenReturn(createExpectedResponse());

        PedidoResponseDTO result = pedidoService.save(request);

        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoRepository).save(pedidoCaptor.capture());
        verify(pedidoMapper).toDto(pedidoCaptor.getValue());

        Pedido savedPedido = pedidoCaptor.getValue();
        assertNotNull(result);
        assertEquals(Estado.PENDIENTE, result.estado());
        assertEquals(FormaPago.TARJETA, result.formaPago());
        assertEquals(1L, result.idUsuario());
        assertEquals(LocalDate.now(), result.fecha());
        assertEquals(350.0, result.total());
        assertEquals(99L, result.id());
        assertEquals(99L, savedPedido.getId());
        assertEquals(350.0, savedPedido.getTotal());
        assertEquals(2, savedPedido.getDetallePedidos().size());

        assertEquals(8, producto1.getStock());
        assertEquals(17, producto2.getStock());

        DetallePedidoResponseDTO detalleProducto1 = findDetalle(result.detalles(), 1L);
        DetallePedidoResponseDTO detalleProducto2 = findDetalle(result.detalles(), 2L);

        assertDetalle(detalleProducto1, 201L, 1L, "Producto 1", 2, 200.0, 8, true);
        assertDetalle(detalleProducto2, 202L, 2L, "Producto 2", 3, 150.0, 17, true);
    }

    @Test
    void saveShouldThrowWhenUserDoesNotExist() {
        PedidoCreateRequestDTO request = createRequest(1L, createDetalleRequest(1L, 2));

        when(usuarioRepository.findByIdAndEliminadoFalse(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.save(request));

        assertEquals("Entidad con id 1 no encontrado", exception.getMessage());
        verify(usuarioRepository).findByIdAndEliminadoFalse(1L);
        verify(productoRepository, never()).findAllById(anyCollection());
        verify(pedidoRepository, never()).save(any());
        verify(pedidoMapper, never()).toDto(any());
    }

    @Test
    void saveShouldThrowWhenProductIsDeleted() {
        Usuario usuario = createUsuario(1L);
        Producto producto = createProducto(1L, "Producto 1", 100.0, 10, true);
        producto.setEliminado(true);

        PedidoCreateRequestDTO request = createRequest(1L, createDetalleRequest(1L, 2));

        when(usuarioRepository.findByIdAndEliminadoFalse(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findAllById(anyCollection())).thenReturn(List.of(producto));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.save(request));

        assertEquals("El producto 'Producto 1' no existe", exception.getMessage());
        verify(usuarioRepository).findByIdAndEliminadoFalse(1L);
        verify(productoRepository).findAllById(anyCollection());
        verify(pedidoRepository, never()).save(any());
        verify(pedidoMapper, never()).toDto(any());
    }

    @Test
    void saveShouldThrowWhenProductIsNotAvailable() {
        Usuario usuario = createUsuario(1L);
        Producto producto = createProducto(1L, "Producto 1", 100.0, 10, false);

        PedidoCreateRequestDTO request = createRequest(1L, createDetalleRequest(1L, 2));

        when(usuarioRepository.findByIdAndEliminadoFalse(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findAllById(anyCollection())).thenReturn(List.of(producto));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> pedidoService.save(request));

        assertEquals("El producto 'Producto 1' no está disponible para la venta", exception.getMessage());
        verify(usuarioRepository).findByIdAndEliminadoFalse(1L);
        verify(productoRepository).findAllById(anyCollection());
        verify(pedidoRepository, never()).save(any());
        verify(pedidoMapper, never()).toDto(any());
    }

    @Test
    void saveShouldThrowWhenProductHasInsufficientStock() {
        Usuario usuario = createUsuario(1L);
        Producto producto = createProducto(1L, "Producto 1", 100.0, 1, true);

        PedidoCreateRequestDTO request = createRequest(1L, createDetalleRequest(1L, 2));

        when(usuarioRepository.findByIdAndEliminadoFalse(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findAllById(anyCollection())).thenReturn(List.of(producto));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> pedidoService.save(request));

        assertEquals("Stock insuficiente para 'Producto 1'. Disponible: 1, Solicitado: 2", exception.getMessage());
        verify(usuarioRepository).findByIdAndEliminadoFalse(1L);
        verify(productoRepository).findAllById(anyCollection());
        verify(pedidoRepository, never()).save(any());
        verify(pedidoMapper, never()).toDto(any());
    }

    private Usuario createUsuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre("Usuario");
        usuario.setApellido("Test");
        usuario.setEmail("usuario@test.com");
        usuario.setCelular("123456789");
        usuario.setContrasena("secret");
        return usuario;
    }

    private void assertDetalle(DetallePedidoResponseDTO detalle,
            Long expectedDetalleId,
            Long expectedId,
            String expectedNombre,
            int expectedCantidad,
            double expectedSubtotal,
            int expectedStock,
            boolean expectedDisponible) {
        assertNotNull(detalle);
        assertEquals(expectedDetalleId, detalle.id());
        assertEquals(expectedCantidad, detalle.cantidad());
        assertEquals(expectedSubtotal, detalle.subtotal());

        ProductoResponseDTO producto = detalle.producto();
        assertNotNull(producto);
        assertEquals(expectedId, producto.id());
        assertEquals(expectedNombre, producto.nombre());
        assertEquals(expectedSubtotal / expectedCantidad, producto.precio());
        assertEquals(expectedNombre + " descripcion", producto.descripcion());
        assertEquals(expectedStock, producto.stock());
        assertEquals(expectedNombre.toLowerCase().replace(" ", "-") + ".jpg", producto.imagen());
        assertEquals(expectedDisponible, producto.disponible());
        assertNull(producto.categoria());
    }

    private DetallePedidoResponseDTO findDetalle(List<DetallePedidoResponseDTO> detalles, Long productoId) {
        return detalles.stream()
                .filter(detalle -> detalle.producto() != null && productoId.equals(detalle.producto().id()))
                .findFirst()
                .orElseThrow();
    }

    private Producto createProducto(Long id, String nombre, double precio, int stock, boolean disponible) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setDescripcion(nombre + " descripcion");
        producto.setStock(stock);
        producto.setImagen(nombre.toLowerCase().replace(" ", "-") + ".jpg");
        producto.setDisponible(disponible);
        return producto;
    }

    private DetallePedidoCreateRequestDTO createDetalleRequest(Long idProducto, int cantidad) {
        return new DetallePedidoCreateRequestDTO(idProducto, cantidad);
    }

    private PedidoCreateRequestDTO createRequest(Long idUsuario, DetallePedidoCreateRequestDTO... detalles) {
        return new PedidoCreateRequestDTO(Estado.PENDIENTE, FormaPago.TARJETA, idUsuario, List.of(detalles));
    }

    private PedidoResponseDTO createExpectedResponse() {
        DetallePedidoResponseDTO detalle1 = new DetallePedidoResponseDTO(
                201L,
                2,
                200.0,
                createExpectedProducto(1L, "Producto 1", 100.0, 8, true));

        DetallePedidoResponseDTO detalle2 = new DetallePedidoResponseDTO(
                202L,
                3,
                150.0,
                createExpectedProducto(2L, "Producto 2", 50.0, 17, true));

        return new PedidoResponseDTO(
                99L,
                LocalDate.now(),
                Estado.PENDIENTE,
                350.0,
                FormaPago.TARJETA,
                1L,
                List.of(detalle1, detalle2));
    }

    private ProductoResponseDTO createExpectedProducto(Long id, String nombre, double precio, int stock,
            boolean disponible) {
        return new ProductoResponseDTO(
                id,
                nombre,
                precio,
                nombre + " descripcion",
                stock,
                nombre.toLowerCase().replace(" ", "-") + ".jpg",
                disponible,
                null);
    }
}
