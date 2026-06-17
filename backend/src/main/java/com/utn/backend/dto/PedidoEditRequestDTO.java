package com.utn.backend.dto;

import com.utn.backend.enums.Estado;
import com.utn.backend.enums.FormaPago;
import com.utn.backend.model.Pedido;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PedidoEditRequest", description = "Datos opcionales para actualizar un pedido")
public record PedidoEditRequestDTO(
        @Schema(description = "Estado del pedido", example = "CONFIRMADO") Estado estado,

        @Schema(description = "Forma de pago", example = "TRANSFERENCIA") FormaPago formaPago
) {
    public void applyTo(Pedido pedido) {
        if (estado != null) {
            pedido.setEstado(estado);
        }

        if (formaPago != null) {
            pedido.setFormaPago(formaPago);
        }
    }
}
