package com.utn.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CategoriaResponse", description = "Respuesta con los datos de una categoria")
public record CategoriaResponseDTO(
        @Schema(description = "ID autogenerado de la categoria", example = "1") Long id,

        @Schema(description = "Nombre de la categoria", example = "Electronica") String nombre,

        @Schema(description = "Descripcion de la categoria", example = "Productos electronicos y gadgets") String descripcion
) {
}
