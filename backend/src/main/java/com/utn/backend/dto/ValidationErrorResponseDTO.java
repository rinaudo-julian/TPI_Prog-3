package com.utn.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "ValidationErrorResponse", description = "Respuesta estandar para errores de validacion")
public record ValidationErrorResponseDTO(
        @Schema(description = "Codigo HTTP del error", example = "400")
        int status,

        @Schema(description = "Mensaje descriptivo del error", example = "Error de validacion")
        String message,

        @Schema(description = "Momento en que ocurrio el error", example = "2026-06-13T12:00:00")
        LocalDateTime timestamp,

        @Schema(description = "Lista de errores por campo", example = "[\"email: debe ser una direccion de correo bien formada\"]")
        String[] errors
) {
}
