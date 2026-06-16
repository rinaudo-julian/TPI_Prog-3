package com.utn.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "ProductoResponse", description = "Respuesta con los datos de un producto")
@Data
public class ProductoResponseDTO {
    @Schema(description = "ID autogenerado del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Laptop Gaming Pro")
    private String nombre;

    @Schema(description = "Precio del producto", example = "1599.99")
    private Double precio;

    @Schema(description = "Descripcion del producto", example = "Laptop de alto rendimiento")
    private String descripcion;

    @Schema(description = "Stock del producto", example = "25")
    private int stock;

    @Schema(description = "Imagen del producto", example = "laptop.jpg")
    private String imagen;

    @Schema(description = "Indica si el producto está disponible", example = "true")
    private boolean disponible;

    @Schema(description = "Categoria asociada al producto")
    private CategoriaResponseDTO categoria;
}
