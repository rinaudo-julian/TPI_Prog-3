package com.utn.backend.service;

import com.utn.backend.dto.ProductoEditRequestDTO;
import com.utn.backend.dto.ProductoResponseDTO;
import com.utn.backend.dto.CategoriaResponseDTO;
import com.utn.backend.exception.ResourceNotFoundException;
import com.utn.backend.mappers.ProductoMapper;
import com.utn.backend.model.Categoria;
import com.utn.backend.model.Producto;
import com.utn.backend.repository.CategoriaRepository;
import com.utn.backend.repository.ProductoRepository;
import com.utn.backend.service.impl.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void updateShouldSaveUpdatedProductWhenRequestIsCompleteAndValid() {
        Categoria categoria = createCategoria(1L, "Electronica", "Productos electronicos");
        Producto producto = createProducto(1L, categoria);

        ProductoEditRequestDTO request = new ProductoEditRequestDTO(
                "Laptop Gaming Pro X",
                1999.99,
                "Laptop de alto rendimiento actualizada",
                30,
                "laptop-x.jpg",
                false,
                1L
        );

        ProductoResponseDTO response = createResponse(producto, categoria);
        response = new ProductoResponseDTO(
                response.id(),
                request.nombre(),
                request.precio(),
                request.descripcion(),
                request.stock(),
                request.imagen(),
                false,
                response.categoria()
        );

        when(productoRepository.findByIdOrThrow(1L)).thenReturn(producto);
        when(productoRepository.existsByNombreAndIdNot(request.nombre(), 1L)).thenReturn(false);
        when(categoriaRepository.findByIdAndEliminadoFalse(1L)).thenReturn(java.util.Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoMapper.toDto(any(Producto.class))).thenReturn(response);

        ProductoResponseDTO result = productoService.update(1L, request);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());

        Producto saved = captor.getValue();
        assertEquals(request.nombre(), saved.getNombre());
        assertEquals(request.precio(), saved.getPrecio());
        assertEquals(request.descripcion(), saved.getDescripcion());
        assertEquals(request.stock(), saved.getStock());
        assertEquals(request.imagen(), saved.getImagen());
        assertFalse(saved.isDisponible());
        assertEquals(categoria.getId(), saved.getCategoria().getId());
        assertEquals(response, result);
    }

    @Test
    void updateShouldChangeOnlyPriceWhenPartialRequestHasOnlyPrice() {
        Categoria categoria = createCategoria(1L, "Electronica", "Productos electronicos");
        Producto producto = createProducto(1L, categoria);

        ProductoEditRequestDTO request = new ProductoEditRequestDTO(null, 150.0, null, null, null, null, null);

        ProductoResponseDTO response = createResponse(producto, categoria);
        response = new ProductoResponseDTO(response.id(), response.nombre(), request.precio(), response.descripcion(), response.stock(), response.imagen(), response.disponible(), response.categoria());

        when(productoRepository.findByIdOrThrow(1L)).thenReturn(producto);
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoMapper.toDto(any(Producto.class))).thenReturn(response);

        ProductoResponseDTO result = productoService.update(1L, request);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());

        Producto saved = captor.getValue();
        assertEquals(producto.getNombre(), saved.getNombre());
        assertEquals(150.0, saved.getPrecio());
        assertEquals(producto.getDescripcion(), saved.getDescripcion());
        assertEquals(producto.getStock(), saved.getStock());
        assertEquals(producto.getImagen(), saved.getImagen());
        assertEquals(producto.isDisponible(), saved.isDisponible());
        assertEquals(producto.getCategoria().getId(), saved.getCategoria().getId());
        assertEquals(response, result);
    }

    @Test
    void updateShouldChangeCategoryWhenNewCategoryExists() {
        Categoria categoriaActual = createCategoria(1L, "Electronica", "Productos electronicos");
        Categoria nuevaCategoria = createCategoria(2L, "Hogar", "Productos para el hogar");
        Producto producto = createProducto(1L, categoriaActual);

        ProductoEditRequestDTO request = new ProductoEditRequestDTO(null, null, null, null, null, null, 2L);

        ProductoResponseDTO response = createResponse(producto, nuevaCategoria);

        when(productoRepository.findByIdOrThrow(1L)).thenReturn(producto);
        when(categoriaRepository.findByIdAndEliminadoFalse(2L)).thenReturn(java.util.Optional.of(nuevaCategoria));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoMapper.toDto(any(Producto.class))).thenReturn(response);

        ProductoResponseDTO result = productoService.update(1L, request);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());

        Producto saved = captor.getValue();
        assertEquals(nuevaCategoria.getId(), saved.getCategoria().getId());
        assertEquals(response, result);
    }

    @Test
    void updateShouldThrowWhenCategoryDoesNotExist() {
        Producto producto = createProducto(1L, createCategoria(1L, "Electronica", "Productos electronicos"));

        ProductoEditRequestDTO request = new ProductoEditRequestDTO(null, null, null, null, null, null, 999L);

        when(productoRepository.findByIdOrThrow(1L)).thenReturn(producto);
        when(categoriaRepository.findByIdAndEliminadoFalse(999L)).thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productoService.update(1L, request));

        assertEquals("La categoría no existe", exception.getMessage());
        verify(productoRepository).findByIdOrThrow(1L);
        verify(categoriaRepository).findByIdAndEliminadoFalse(999L);
        verify(productoRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowWhenProductNameAlreadyExists() {
        Producto producto = createProducto(1L, createCategoria(1L, "Electronica", "Productos electronicos"));

        ProductoEditRequestDTO request = new ProductoEditRequestDTO("Laptop Gaming Pro", null, null, null, null, null, null);

        when(productoRepository.findByIdOrThrow(1L)).thenReturn(producto);
        when(productoRepository.existsByNombreAndIdNot(request.nombre(), 1L)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> productoService.update(1L, request));

        assertEquals("Ya existe un producto con ese nombre", exception.getMessage());
        verify(productoRepository).findByIdOrThrow(1L);
        verify(productoRepository).existsByNombreAndIdNot(request.nombre(), 1L);
        verify(categoriaRepository, never()).findByIdAndEliminadoFalse(anyLong());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowWhenProductDoesNotExist() {
        ProductoEditRequestDTO request = new ProductoEditRequestDTO("Laptop Gaming Pro", null, null, null, null, null, null);

        when(productoRepository.findByIdOrThrow(1L)).thenThrow(new ResourceNotFoundException());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productoService.update(1L, request));

        assertEquals("Recurso no encontrado", exception.getMessage());
        verify(productoRepository).findByIdOrThrow(1L);
        verify(productoRepository, never()).existsByNombreAndIdNot(anyString(), anyLong());
        verify(categoriaRepository, never()).findByIdAndEliminadoFalse(anyLong());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void updateShouldSaveWithoutChangesWhenRequestIsEmpty() {
        Categoria categoria = createCategoria(1L, "Electronica", "Productos electronicos");
        Producto producto = createProducto(1L, categoria);

        ProductoEditRequestDTO request = new ProductoEditRequestDTO(null, null, null, null, null, null, null);

        ProductoResponseDTO response = createResponse(producto, categoria);

        when(productoRepository.findByIdOrThrow(1L)).thenReturn(producto);
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoMapper.toDto(any(Producto.class))).thenReturn(response);

        ProductoResponseDTO result = productoService.update(1L, request);

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());

        Producto saved = captor.getValue();
        assertEquals(producto.getNombre(), saved.getNombre());
        assertEquals(producto.getPrecio(), saved.getPrecio());
        assertEquals(producto.getDescripcion(), saved.getDescripcion());
        assertEquals(producto.getStock(), saved.getStock());
        assertEquals(producto.getImagen(), saved.getImagen());
        assertEquals(producto.isDisponible(), saved.isDisponible());
        assertEquals(producto.getCategoria().getId(), saved.getCategoria().getId());
        assertEquals(response, result);
    }

    private Categoria createCategoria(Long id, String nombre, String descripcion) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        return categoria;
    }

    private Producto createProducto(Long id, Categoria categoria) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre("Laptop Gaming Pro");
        producto.setPrecio(100.0);
        producto.setDescripcion("Laptop de alto rendimiento");
        producto.setStock(25);
        producto.setImagen("laptop.jpg");
        producto.setDisponible(true);
        producto.setCategoria(categoria);
        return producto;
    }

    private ProductoResponseDTO createResponse(Producto producto, Categoria categoria) {
        CategoriaResponseDTO categoriaDTO = new CategoriaResponseDTO(categoria.getId(), categoria.getNombre(), categoria.getDescripcion());
        return new ProductoResponseDTO(
                producto.getId(),
                producto.getNombre(),
                producto.getPrecio(),
                producto.getDescripcion(),
                producto.getStock(),
                producto.getImagen(),
                producto.isDisponible(),
                categoriaDTO
        );
    }
}
