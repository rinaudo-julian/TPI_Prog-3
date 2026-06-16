package com.utn.backend.dto;

import com.utn.backend.enums.Rol;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UsuarioResponse", description = "Representacion publica del usuario creado")
public record UsuarioResponseDTO(
                @Schema(description = "Identificador unico del usuario", example = "1") Long id,

                @Schema(description = "Nombre del usuario", example = "Juan") String nombre,

                @Schema(description = "Apellido del usuario", example = "Perez") String apellido,

                @Schema(description = "Correo electronico del usuario", example = "juan.perez@mail.com") String mail,

                @Schema(description = "Numero de celular", example = "+5491122334455") String celular,

                @Schema(description = "Rol asignado al usuario", example = "USUARIO") Rol rol) {
}
