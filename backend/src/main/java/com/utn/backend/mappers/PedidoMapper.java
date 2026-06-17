package com.utn.backend.mappers;

import com.utn.backend.dto.PedidoResponseDTO;
import com.utn.backend.model.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = DetallePedidoMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PedidoMapper {
    @Mapping(target = "idUsuario", source = "usuario.id")
    @Mapping(target = "detalles", source = "detallePedidos")
    PedidoResponseDTO toDto(Pedido pedido);
}
