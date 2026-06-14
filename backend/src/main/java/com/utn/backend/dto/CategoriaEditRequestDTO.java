package com.utn.backend.dto;

import com.utn.backend.model.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(name = "CategoriaEditRequest", description = "Datos opcionales para actualizar una categoria")
@Data
public class CategoriaEditRequestDTO {
    @Schema(description = "Nombre de la categoria", example = "Electronica")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Schema(description = "Descripcion de la categoria", example = "Productos electronicos y gadgets")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    public void applyTo(Categoria categoria) {
        if (nombre != null) {
            categoria.setNombre(nombre);
        }

        if (descripcion != null) {
            categoria.setDescripcion(descripcion);
        }
    }
}
