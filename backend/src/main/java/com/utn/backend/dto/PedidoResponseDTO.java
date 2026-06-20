package com.utn.backend.dto;

import com.utn.backend.enums.Estado;
import com.utn.backend.enums.FormaPago;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "PedidoResponse", description = "Respuesta con los datos de un pedido")
public record PedidoResponseDTO(
        @Schema(description = "ID autogenerado del pedido", example = "1") Long id,

        @Schema(description = "Fecha de creación", example = "2024-12-10") LocalDate fecha,

        @Schema(description = "Estado del pedido", example = "PENDIENTE") Estado estado,

        @Schema(description = "Total del pedido", example = "350.00") Double total,

        @Schema(description = "Teléfono de contacto", example = "+54 9 261 123 4567") String telefono,

        @Schema(description = "Dirección de entrega", example = "San Martín 123") String direccion,

        @Schema(description = "Nota adicional", example = "Tocar timbre") String notaAdicional,

        @Schema(description = "Forma de pago", example = "TARJETA") FormaPago formaPago,

        @Schema(description = "ID del usuario asociado", example = "1") Long idUsuario,

        @Schema(description = "Detalles del pedido") List<DetallePedidoResponseDTO> detalles
) {
}
