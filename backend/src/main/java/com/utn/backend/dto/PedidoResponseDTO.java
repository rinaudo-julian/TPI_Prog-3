package com.utn.backend.dto;

import com.utn.backend.enums.Estado;
import com.utn.backend.enums.FormaPago;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Schema(name = "PedidoResponse", description = "Respuesta con los datos de un pedido")
@Data
public class PedidoResponseDTO {
    @Schema(description = "ID autogenerado del pedido", example = "1")
    private Long id;

    @Schema(description = "Fecha de creación", example = "2024-12-10")
    private LocalDate fecha;

    @Schema(description = "Estado del pedido", example = "PENDIENTE")
    private Estado estado;

    @Schema(description = "Total del pedido", example = "350.00")
    private Double total;

    @Schema(description = "Forma de pago", example = "TARJETA")
    private FormaPago formaPago;

    @Schema(description = "ID del usuario asociado", example = "1")
    private Long idUsuario;

    @Schema(description = "Detalles del pedido")
    private List<DetallePedidoResponseDTO> detalles;
}
