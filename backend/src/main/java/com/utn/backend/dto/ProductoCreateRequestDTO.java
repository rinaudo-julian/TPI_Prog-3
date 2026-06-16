package com.utn.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(name = "ProductoCreateRequest", description = "Datos necesarios para crear un producto")
@Data
public class ProductoCreateRequestDTO {
    @Schema(description = "Nombre del producto", example = "Laptop Gaming Pro")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Schema(description = "Precio del producto", example = "1599.99")
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", inclusive = false, message = "El precio debe ser mayor a 0")
    private Double precio;

    @Schema(description = "Descripcion del producto", example = "Laptop de alto rendimiento")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @Schema(description = "Stock inicial del producto", example = "25")
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Schema(description = "Imagen del producto", example = "laptop.jpg")
    private String imagen;

    @Schema(description = "Indica si el producto está disponible", example = "true")
    private Boolean disponible;

    @Schema(description = "Identificador de la categoria", example = "1")
    @NotNull(message = "La categoría es obligatoria")
    private Long idCategoria;
}
