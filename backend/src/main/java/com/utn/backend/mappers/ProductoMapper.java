package com.utn.backend.mappers;

import com.utn.backend.dto.ProductoCreateRequestDTO;
import com.utn.backend.dto.ProductoResponseDTO;
import com.utn.backend.model.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = CategoriaMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductoMapper {
    @Mapping(target = "categoria", ignore = true)
    Producto toEntity(ProductoCreateRequestDTO productoCreateRequestDTO);

    ProductoResponseDTO toDto(Producto producto);
}
