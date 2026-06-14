package com.utn.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "CategoriaResponse", description = "Respuesta con los datos de una categoria")
@Data
public class CategoriaResponseDTO {
    @Schema(description = "ID autogenerado de la categoria", example = "1")
    private Long id;

    @Schema(description = "Nombre de la categoria", example = "Electronica")
    private String nombre;

    @Schema(description = "Descripcion de la categoria", example = "Productos electronicos y gadgets")
    private String descripcion;
}
