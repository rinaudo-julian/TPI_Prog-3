package com.utn.backend.dto;

import com.utn.backend.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(name = "UsuarioEditRequest", description = "Datos opcionales para actualizar un usuario")
public record UsuarioEditRequestDTO(
        @Schema(description = "Nombre del usuario", example = "Juan")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres") String nombre,

        @Schema(description = "Apellido del usuario", example = "Perez")
        @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres") String apellido,

        @Schema(description = "Correo electronico del usuario", example = "juan.perez@mail.com")
        @Email(message = "El email debe tener un formato válido") String email,

        @Schema(description = "Numero de celular", example = "+5491122334455")
        @Size(max = 20, message = "El celular no puede tener más de 20 caracteres") String celular,

        @Schema(description = "Nueva contrasena del usuario", example = "NuevaClave123", accessMode = Schema.AccessMode.WRITE_ONLY)
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") String password
) {
    public void applyTo(Usuario usuario) {
        if (nombre != null) {
            usuario.setNombre(nombre);
        }

        if (apellido != null) {
            usuario.setApellido(apellido);
        }

        if (email != null) {
            usuario.setEmail(email);
        }

        if (celular != null) {
            usuario.setCelular(celular);
        }
    }
}
