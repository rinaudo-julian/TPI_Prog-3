package com.utn.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProductoResponse", description = "Respuesta con los datos de un producto")
public record ProductoResponseDTO(
        @Schema(description = "ID autogenerado del producto", example = "1") Long id,

        @Schema(description = "Nombre del producto", example = "Laptop Gaming Pro") String nombre,

        @Schema(description = "Precio del producto", example = "1599.99") Double precio,

        @Schema(description = "Descripcion del producto", example = "Laptop de alto rendimiento") String descripcion,

        @Schema(description = "Stock del producto", example = "25") int stock,

        @Schema(description = "Imagen del producto", example = "laptop.jpg") String imagen,

        @Schema(description = "Indica si el producto está disponible", example = "true") boolean disponible,

        @Schema(description = "Categoria asociada al producto") CategoriaResponseDTO categoria
) {
}
