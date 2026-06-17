package com.utn.backend.service.impl;

import com.utn.backend.dto.ProductoCreateRequestDTO;
import com.utn.backend.dto.ProductoEditRequestDTO;
import com.utn.backend.dto.ProductoResponseDTO;
import com.utn.backend.exception.ResourceNotFoundException;
import com.utn.backend.mappers.ProductoMapper;
import com.utn.backend.model.Categoria;
import com.utn.backend.model.Producto;
import com.utn.backend.repository.CategoriaRepository;
import com.utn.backend.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    public ProductoResponseDTO save(ProductoCreateRequestDTO requestDTO) {
        Categoria categoria = categoriaRepository.findByIdAndEliminadoFalse(requestDTO.idCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("La categoría no existe"));

        Producto producto = productoMapper.toEntity(requestDTO);
        producto.setCategoria(categoria);
        producto.setDisponible(requestDTO.disponible() == null || requestDTO.disponible());

        producto = productoRepository.save(producto);

        return productoMapper.toDto(producto);
    }

    public ProductoResponseDTO update(Long id, ProductoEditRequestDTO requestDTO) {
        Producto producto = productoRepository.findByIdOrThrow(id);

        if (requestDTO.nombre() != null
                && productoRepository.existsByNombreAndIdNot(requestDTO.nombre(), id)) {
            throw new IllegalStateException("Ya existe un producto con ese nombre");
        }

        if (requestDTO.idCategoria() != null) {
            Categoria categoria = categoriaRepository.findByIdAndEliminadoFalse(requestDTO.idCategoria())
                    .orElseThrow(() -> new ResourceNotFoundException("La categoría no existe"));
            producto.setCategoria(categoria);
        }

        requestDTO.applyTo(producto);
        producto = productoRepository.save(producto);

        return productoMapper.toDto(producto);
    }

    public List<ProductoResponseDTO> findAll() {
        return productoRepository.findAll().stream()
                .map(productoMapper::toDto)
                .toList();
    }

    public ProductoResponseDTO findById(Long id) {
        Producto producto = productoRepository.findByIdOrThrow(id);
        return productoMapper.toDto(producto);
    }

    public List<ProductoResponseDTO> findByCategoriaId(Long categoriaId) {
        categoriaRepository.findByIdAndEliminadoFalse(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("La categoría no existe"));

        return productoRepository.findAllByCategoriaIdAndEliminadoFalse(categoriaId).stream()
                .map(productoMapper::toDto)
                .toList();
    }

    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }
}
