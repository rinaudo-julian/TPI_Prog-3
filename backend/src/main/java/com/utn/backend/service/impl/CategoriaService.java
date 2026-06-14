package com.utn.backend.service.impl;

import com.utn.backend.dto.CategoriaCreateRequestDTO;
import com.utn.backend.dto.CategoriaResponseDTO;
import com.utn.backend.mappers.CategoriaMapper;
import com.utn.backend.model.Categoria;
import com.utn.backend.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
