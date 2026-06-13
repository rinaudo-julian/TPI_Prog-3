package com.utn.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoriaRequestDTO {
    @NotNull
    @NotBlank
    @Size(min = 2, max = 100)
    private String nombre;

    @NotBlank
    @Size(max = 500)
    private String descripcion;
}
