package com.utn.backend.dto;

import com.utn.backend.enums.Estado;
import com.utn.backend.enums.FormaPago;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(name = "PedidoCreateRequest", description = "Datos necesarios para crear un pedido")
public record PedidoCreateRequestDTO(
        @Schema(description = "Estado del pedido", example = "PENDIENTE")
        @NotNull(message = "El estado es obligatorio") Estado estado,

        @Schema(description = "Forma de pago", example = "TARJETA")
        @NotNull(message = "La forma de pago es obligatoria") FormaPago formaPago,

        @Schema(description = "Teléfono de contacto", example = "+54 9 261 123 4567")
        @NotBlank(message = "El teléfono es obligatorio") String telefono,

        @Schema(description = "Dirección de entrega", example = "San Martín 123")
        @NotBlank(message = "La dirección es obligatoria") String direccion,

        @Schema(description = "Nota adicional", example = "Tocar timbre") String notaAdicional,

        @Schema(description = "Identificador del usuario", example = "1")
        @NotNull(message = "El usuario es obligatorio") Long idUsuario,

        @Schema(description = "Detalles del pedido")
        @NotEmpty(message = "Se requiere al menos un detalle")
        @Valid List<DetallePedidoCreateRequestDTO> detallePedido
) {
}
