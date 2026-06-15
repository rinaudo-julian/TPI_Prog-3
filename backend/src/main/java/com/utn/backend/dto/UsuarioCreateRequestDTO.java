package com.utn.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "UsuarioCreateRequest", description = "Datos necesarios para crear un usuario")
public record UsuarioCreateRequestDTO(

        @Schema(description = "Nombre del usuario", example = "Juan")
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres") String nombre,

        @Schema(description = "Apellido del usuario", example = "Perez")
        @NotBlank(message = "El apellido no puede estar vacío")
        @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres") String apellido,

        @Schema(description = "Correo electronico del usuario", example = "juan.perez@mail.com")
        @NotBlank(message = "El email no puede estar vacío")
        @Email(message = "El email debe tener un formato válido") String email,

        @Schema(description = "Numero de celular", example = "+5491122334455")
        @Size(max = 20, message = "El celular no puede tener más de 20 caracteres") String celular,

        @Schema(description = "Contrasena inicial del usuario", example = "Secreta123", accessMode = Schema.AccessMode.WRITE_ONLY)
        @NotBlank(message = "La contraseña no puede estar vacía")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") String password

) {
}
