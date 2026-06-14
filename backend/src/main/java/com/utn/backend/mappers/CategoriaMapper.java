package com.utn.backend.mappers;

import com.utn.backend.dto.CategoriaCreateRequestDTO;
import com.utn.backend.dto.CategoriaResponseDTO;
import com.utn.backend.model.Categoria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {
    Categoria toEntity(CategoriaCreateRequestDTO categoriaCreateRequestDTO);

    CategoriaResponseDTO toDto(Categoria categoria);
}
