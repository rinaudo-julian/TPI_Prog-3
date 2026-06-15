package com.utn.backend.service;

import com.utn.backend.dto.CategoriaCreateRequestDTO;
import com.utn.backend.dto.CategoriaEditRequestDTO;
import com.utn.backend.dto.CategoriaResponseDTO;
import com.utn.backend.exception.ResourceNotFoundException;
import com.utn.backend.mappers.CategoriaMapper;
import com.utn.backend.model.Categoria;
import com.utn.backend.repository.CategoriaRepository;
import com.utn.backend.service.impl.CategoriaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void createShouldSaveCategoryWhenNameDoesNotExist() {
        CategoriaCreateRequestDTO request = new CategoriaCreateRequestDTO();
        request.setNombre("Electronica");
        request.setDescripcion("Productos electronicos");

        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        CategoriaResponseDTO response = new CategoriaResponseDTO();
        response.setId(1L);
        response.setNombre(request.getNombre());
        response.setDescripcion(request.getDescripcion());

        when(categoriaRepository.existsByNombre(request.getNombre())).thenReturn(false);
        when(categoriaMapper.toEntity(request)).thenReturn(categoria);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        when(categoriaMapper.toDto(categoria)).thenReturn(response);

        CategoriaResponseDTO result = categoriaService.create(request);

        verify(categoriaRepository).existsByNombre(request.getNombre());
        verify(categoriaRepository).save(categoria);
        verify(categoriaMapper).toEntity(request);
        verify(categoriaMapper).toDto(categoria);
        assertEquals(response, result);
    }

    @Test
    void createShouldThrowWhenNameAlreadyExists() {
        CategoriaCreateRequestDTO request = new CategoriaCreateRequestDTO();
        request.setNombre("Electronica");

        when(categoriaRepository.existsByNombre(request.getNombre())).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> categoriaService.create(request));

        assertEquals("Ya existe una categoría con ese nombre", exception.getMessage());
        verify(categoriaRepository).existsByNombre(request.getNombre());
        verify(categoriaMapper, never()).toEntity(any());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void findAllShouldReturnMappedCategoryDtos() {
        Categoria categoria1 = new Categoria();
        categoria1.setNombre("Electronica");
        categoria1.setDescripcion("Productos electronicos");

        Categoria categoria2 = new Categoria();
        categoria2.setNombre("Hogar");
        categoria2.setDescripcion("Productos para el hogar");

        CategoriaResponseDTO dto1 = new CategoriaResponseDTO();
        dto1.setId(1L);
        dto1.setNombre(categoria1.getNombre());
        dto1.setDescripcion(categoria1.getDescripcion());

        CategoriaResponseDTO dto2 = new CategoriaResponseDTO();
        dto2.setId(2L);
        dto2.setNombre(categoria2.getNombre());
        dto2.setDescripcion(categoria2.getDescripcion());

        when(categoriaRepository.findAllByEliminadoFalse()).thenReturn(List.of(categoria1, categoria2));
        when(categoriaMapper.toDto(categoria1)).thenReturn(dto1);
        when(categoriaMapper.toDto(categoria2)).thenReturn(dto2);

        List<CategoriaResponseDTO> result = categoriaService.findAll();

        assertEquals(List.of(dto1, dto2), result);
        verify(categoriaRepository).findAllByEliminadoFalse();
        verify(categoriaMapper).toDto(categoria1);
        verify(categoriaMapper).toDto(categoria2);
    }

    @Test
    void findByIdShouldReturnCategoryDtoWhenCategoryExists() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Electronica");
        categoria.setDescripcion("Productos electronicos");

        CategoriaResponseDTO response = new CategoriaResponseDTO();
        response.setId(1L);
        response.setNombre(categoria.getNombre());
        response.setDescripcion(categoria.getDescripcion());

        when(categoriaRepository.findByIdOrThrow(1L)).thenReturn(categoria);
        when(categoriaMapper.toDto(categoria)).thenReturn(response);

        CategoriaResponseDTO result = categoriaService.findById(1L);

        assertEquals(response, result);
        verify(categoriaRepository).findByIdOrThrow(1L);
        verify(categoriaMapper).toDto(categoria);
    }

    @Test
    void findByIdShouldThrowWhenCategoryDoesNotExist() {
        when(categoriaRepository.findByIdOrThrow(1L)).thenThrow(new ResourceNotFoundException());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> categoriaService.findById(1L));

        assertEquals("Recurso no encontrado", exception.getMessage());
        verify(categoriaRepository).findByIdOrThrow(1L);
        verify(categoriaMapper, never()).toDto(any());
    }

    @Test
    void updateShouldThrowWhenCategoryIdDoesNotExist() {
        CategoriaEditRequestDTO request = new CategoriaEditRequestDTO();
        request.setNombre("Hogar");

        when(categoriaRepository.findByIdOrThrow(1L)).thenThrow(new ResourceNotFoundException());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> categoriaService.update(1L, request));

        assertEquals("Recurso no encontrado", exception.getMessage());
        verify(categoriaRepository).findByIdOrThrow(1L);
        verify(categoriaRepository, never()).existsByNombreAndIdNot(any(), any());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void updateShouldSaveUpdatedCategoryWhenNameDoesNotExist() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Electronica");
        categoria.setDescripcion("Productos electronicos");

        CategoriaEditRequestDTO request = new CategoriaEditRequestDTO();
        request.setNombre("Hogar");
        request.setDescripcion("Productos para el hogar");

        CategoriaResponseDTO response = new CategoriaResponseDTO();
        response.setId(1L);
        response.setNombre(request.getNombre());
        response.setDescripcion(request.getDescripcion());

        when(categoriaRepository.findByIdOrThrow(1L)).thenReturn(categoria);
        when(categoriaRepository.existsByNombreAndIdNot(request.getNombre(), 1L)).thenReturn(false);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        when(categoriaMapper.toDto(categoria)).thenReturn(response);

        CategoriaResponseDTO result = categoriaService.update(1L, request);

        ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);
        verify(categoriaRepository).save(captor.capture());
        Categoria saved = captor.getValue();

        assertEquals(request.getNombre(), saved.getNombre());
        assertEquals(request.getDescripcion(), saved.getDescripcion());
        assertEquals(response, result);
    }

    @Test
    void updateShouldThrowWhenUpdatedNameAlreadyExists() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Electronica");
        categoria.setDescripcion("Productos electronicos");

        CategoriaEditRequestDTO request = new CategoriaEditRequestDTO();
        request.setNombre("Hogar");

        when(categoriaRepository.findByIdOrThrow(1L)).thenReturn(categoria);
        when(categoriaRepository.existsByNombreAndIdNot(request.getNombre(), 1L)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> categoriaService.update(1L, request));

        assertEquals("Ya existe una categoría con ese nombre", exception.getMessage());
        verify(categoriaRepository).findByIdOrThrow(1L);
        verify(categoriaRepository).existsByNombreAndIdNot(request.getNombre(), 1L);
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void deleteShouldDelegateToRepositoryDeleteById() {
        categoriaService.delete(1L);

        verify(categoriaRepository).deleteById(1L);
    }
}
