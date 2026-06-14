package com.utn.backend.service.impl;

import com.utn.backend.dto.CategoriaCreateRequestDTO;
import com.utn.backend.dto.CategoriaEditRequestDTO;
import com.utn.backend.dto.CategoriaResponseDTO;
import com.utn.backend.mappers.CategoriaMapper;
import com.utn.backend.model.Categoria;
import com.utn.backend.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaResponseDTO create (CategoriaCreateRequestDTO categoriaRequestDTO) {
        if (categoriaRepository.existsByNombre(categoriaRequestDTO.getNombre())) {
            throw new IllegalStateException("Ya existe una categoría con ese nombre");
        }

        Categoria categoria = categoriaMapper.toEntity(categoriaRequestDTO);

        categoria = categoriaRepository.save(categoria);

        return categoriaMapper.toDto(categoria);
    }

    public List<CategoriaResponseDTO> findAll() {
        return categoriaRepository.findAllByEliminadoFalse().stream()
                .map(categoriaMapper::toDto)
                .toList();
    }

    public CategoriaResponseDTO findById(Long id) {
        Categoria categoria = categoriaRepository.findByIdOrThrow(id);
        return categoriaMapper.toDto(categoria);
    }

    public CategoriaResponseDTO update(Long id, CategoriaEditRequestDTO categoriaEditRequestDTO) {
        Categoria categoria = categoriaRepository.findByIdOrThrow(id);

        if (categoriaEditRequestDTO.getNombre() != null
                && categoriaRepository.existsByNombreAndIdNot(categoriaEditRequestDTO.getNombre(), id)) {
            throw new IllegalStateException("Ya existe una categoría con ese nombre");
        }

        categoriaEditRequestDTO.applyTo(categoria);
        categoria = categoriaRepository.save(categoria);

        return categoriaMapper.toDto(categoria);
    }
}
