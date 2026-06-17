package com.utn.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "DetallePedidoResponse", description = "Detalle de pedido con producto asociado")
@Data
public class DetallePedidoResponseDTO {
    @Schema(description = "ID autogenerado del detalle", example = "1")
    private Long id;

    @Schema(description = "Cantidad del producto", example = "2")
    private int cantidad;

    @Schema(description = "Subtotal del detalle", example = "200.00")
    private Double subtotal;

    @Schema(description = "Producto asociado al detalle")
    private ProductoResponseDTO producto;
}
