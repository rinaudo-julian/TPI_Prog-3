package com.utn.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "LoginRequest", description = "Credenciales necesarias para iniciar sesion")
public record LoginRequestDTO(
        @Schema(description = "Correo electronico del usuario", example = "juan.perez@mail.com")
        @NotBlank(message = "El email no puede estar vacío")
        @Email(message = "El email debe tener un formato válido") String email,

        @Schema(description = "Contrasena del usuario", example = "Secreta123", accessMode = Schema.AccessMode.WRITE_ONLY)
        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") String password
) {
}
