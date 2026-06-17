package com.utn.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DetallePedidoResponse", description = "Detalle de pedido con producto asociado")
public record DetallePedidoResponseDTO(
        @Schema(description = "ID autogenerado del detalle", example = "1") Long id,

        @Schema(description = "Cantidad del producto", example = "2") int cantidad,

        @Schema(description = "Subtotal del detalle", example = "200.00") Double subtotal,

        @Schema(description = "Producto asociado al detalle") ProductoResponseDTO producto
) {
}
