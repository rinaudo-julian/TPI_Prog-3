package com.utn.backend.service.impl;

import com.utn.backend.dto.CategoriaRequestDTO;
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

    public CategoriaResponseDTO create (CategoriaRequestDTO categoriaRequestDTO) {
        Categoria categoria = categoriaMapper.toEntity(categoriaRequestDTO);

        categoria = categoriaRepository.save(categoria);

        return categoriaMapper.toDto(categoria);
    }
}
