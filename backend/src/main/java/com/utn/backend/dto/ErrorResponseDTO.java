package com.utn.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "ErrorResponse", description = "Respuesta estandar para errores de la API")
public record ErrorResponseDTO(
        @Schema(description = "Codigo HTTP del error", example = "404")
        int status,

        @Schema(description = "Mensaje descriptivo del error", example = "Recurso no encontrado")
        String message,

        @Schema(description = "Momento en que ocurrio el error", example = "2026-06-13T12:00:00")
        LocalDateTime timestamp
) {
}
