package com.utn.backend.mappers;

import com.utn.backend.dto.CategoriaRequestDTO;
import com.utn.backend.dto.CategoriaResponseDTO;
import com.utn.backend.model.Categoria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {
    Categoria toEntity(CategoriaRequestDTO categoriaRequestDTO);

    CategoriaResponseDTO toDto(Categoria categoria);
}
