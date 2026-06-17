package com.utn.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(name = "DetallePedidoCreateRequest", description = "Detalle de producto para crear un pedido")
public record DetallePedidoCreateRequestDTO(
        @Schema(description = "Identificador del producto", example = "1")
        @NotNull(message = "El producto es obligatorio") Long idProducto,

        @Schema(description = "Cantidad solicitada", example = "2")
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1") Integer cantidad
) {
}
