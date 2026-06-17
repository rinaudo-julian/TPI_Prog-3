package com.utn.backend.mappers;

import com.utn.backend.dto.DetallePedidoResponseDTO;
import com.utn.backend.model.DetallePedido;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = ProductoMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DetallePedidoMapper {
    DetallePedidoResponseDTO toDto(DetallePedido detallePedido);
}
