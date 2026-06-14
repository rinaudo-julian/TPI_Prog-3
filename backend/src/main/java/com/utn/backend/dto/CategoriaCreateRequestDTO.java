package com.utn.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(name = "CategoriaCreateRequest", description = "Datos necesarios para crear una categoria")
@Data
public class CategoriaCreateRequestDTO {
    @Schema(description = "Nombre de la categoria", example = "Electronica")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Schema(description = "Descripcion opcional de la categoria", example = "Productos electronicos y gadgets")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
}
