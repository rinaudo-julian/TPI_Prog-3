package com.utn.backend.dto;

import com.utn.backend.model.Producto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@Schema(name = "ProductoEditRequest", description = "Datos opcionales para actualizar un producto")
public record ProductoEditRequestDTO(
        @Schema(description = "Nombre del producto", example = "Laptop Gaming Pro")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres") String nombre,

        @Schema(description = "Precio del producto", example = "1599.99")
        @DecimalMin(value = "0.01", inclusive = false, message = "El precio debe ser mayor a 0") Double precio,

        @Schema(description = "Descripcion del producto", example = "Laptop de alto rendimiento")
        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres") String descripcion,

        @Schema(description = "Stock del producto", example = "25")
        @Min(value = 0, message = "El stock no puede ser negativo") Integer stock,

        @Schema(description = "Imagen del producto", example = "laptop.jpg") String imagen,

        @Schema(description = "Indica si el producto está disponible", example = "false") Boolean disponible,

        @Schema(description = "Identificador de la categoria", example = "2") Long idCategoria
) {
    public void applyTo(Producto producto) {
        if (nombre != null) {
            producto.setNombre(nombre);
        }

        if (precio != null) {
            producto.setPrecio(precio);
        }

        if (descripcion != null) {
            producto.setDescripcion(descripcion);
        }

        if (stock != null) {
            producto.setStock(stock);
        }

        if (imagen != null) {
            producto.setImagen(imagen);
        }

        if (disponible != null) {
            producto.setDisponible(disponible);
        }
    }
}
