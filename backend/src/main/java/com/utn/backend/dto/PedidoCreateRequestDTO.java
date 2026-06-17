package com.utn.backend.dto;

import com.utn.backend.enums.Estado;
import com.utn.backend.enums.FormaPago;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(name = "PedidoCreateRequest", description = "Datos necesarios para crear un pedido")
public record PedidoCreateRequestDTO(
        @Schema(description = "Estado del pedido", example = "PENDIENTE")
        @NotNull(message = "El estado es obligatorio") Estado estado,

        @Schema(description = "Forma de pago", example = "TARJETA")
        @NotNull(message = "La forma de pago es obligatoria") FormaPago formaPago,

        @Schema(description = "Identificador del usuario", example = "1")
        @NotNull(message = "El usuario es obligatorio") Long idUsuario,

        @Schema(description = "Detalles del pedido")
        @NotEmpty(message = "Se requiere al menos un detalle")
        @Valid List<DetallePedidoCreateRequestDTO> detallePedido
) {
}
