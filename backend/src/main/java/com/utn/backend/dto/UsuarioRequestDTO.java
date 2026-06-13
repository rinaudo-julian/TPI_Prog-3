package com.utn.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "UsuarioRequest", description = "Datos necesarios para crear un usuario")
public record UsuarioRequestDTO(

        @Schema(description = "Nombre del usuario", example = "Juan")
        @NotBlank @Size(min = 2, max = 50) String nombre,

        @Schema(description = "Apellido del usuario", example = "Perez")
        @NotBlank @Size(min = 2, max = 50) String apellido,

        @Schema(description = "Correo electronico del usuario", example = "juan.perez@mail.com")
        @NotBlank @Email String email,

        @Schema(description = "Numero de celular", example = "+5491122334455")
        @Size(max = 20) String celular,

        @Schema(description = "Contrasena inicial del usuario", example = "Secreta123", accessMode = Schema.AccessMode.WRITE_ONLY)
        @NotBlank @Size(min = 6) String password

) {
}
