package com.utn.backend.controller;

import com.utn.backend.dto.UsuarioCreateRequestDTO;
import com.utn.backend.dto.UsuarioEditRequestDTO;
import com.utn.backend.dto.UsuarioResponseDTO;
import com.utn.backend.service.impl.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones para crear y administrar usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping
    @Operation(
            summary = "Listar usuarios",
            description = "Retorna todos los usuarios activos del sistema sin exponer contraseñas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuarios listados correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ErrorResponseDTO.class),
                            examples = {@ExampleObject(
                                    name = "Error interno",
                                    value = """
                                            {
                                              "status": 500,
                                              "message": "Error interno del servidor",
                                              "timestamp": "2026-06-14T21:00:42.290Z"
                                            }
                                            """
                            )}
                    )
            )
    })
    public ResponseEntity<List<UsuarioResponseDTO>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener usuario por id",
            description = "Retorna un usuario activo por su identificador sin exponer contraseñas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ErrorResponseDTO.class),
                            examples = {@ExampleObject(
                                    name = "Usuario inexistente",
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "Recurso no encontrado",
                                              "timestamp": "2026-06-14T21:00:42.290Z"
                                            }
                                            """
                            )}
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ErrorResponseDTO.class),
                            examples = {@ExampleObject(
                                    name = "Error interno",
                                    value = """
                                            {
                                              "status": 500,
                                              "message": "Error interno del servidor",
                                              "timestamp": "2026-06-14T21:00:42.290Z"
                                            }
                                            """
                            )}
                    )
            )
    })
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza parcial o completamente los datos de un usuario sin exponer contraseñas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error de validacion en los datos enviados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ValidationErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ErrorResponseDTO.class),
                            examples = {@ExampleObject(
                                    name = "Usuario inexistente",
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "Recurso no encontrado",
                                              "timestamp": "2026-06-14T21:00:42.290Z"
                                            }
                                            """
                            )}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "El email ya existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ErrorResponseDTO.class),
                            examples = {@ExampleObject(
                                    name = "Email duplicado",
                                    value = """
                                            {
                                              "status": 400,
                                              "message": "El email ya está registrado",
                                              "timestamp": "2026-06-14T21:00:42.290Z"
                                            }
                                            """
                            )}
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ErrorResponseDTO.class),
                            examples = {@ExampleObject(
                                    name = "Error interno",
                                    value = """
                                            {
                                              "status": 500,
                                              "message": "Error interno del servidor",
                                              "timestamp": "2026-06-14T21:00:42.290Z"
                                            }
                                            """
                            )}
                    )
            )
    })
    public ResponseEntity<UsuarioResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UsuarioEditRequestDTO requestDTO) {
        return ResponseEntity.ok(usuarioService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar usuario",
            description = "Realiza una eliminacion logica de un usuario existente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ErrorResponseDTO.class),
                            examples = {@ExampleObject(
                                    name = "Usuario inexistente",
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "Recurso no encontrado",
                                              "timestamp": "2026-06-14T21:00:42.290Z"
                                            }
                                            """
                            )}
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ErrorResponseDTO.class),
                            examples = {@ExampleObject(
                                    name = "Error interno",
                                    value = """
                                            {
                                              "status": 500,
                                              "message": "Error interno del servidor",
                                              "timestamp": "2026-06-14T21:00:42.290Z"
                                            }
                                            """
                            )}
                    )
            )
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(
            summary = "Crear usuario",
            description = "Registra un nuevo usuario en el sistema y devuelve el usuario creado sin exponer la contrasena."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UsuarioResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error de validacion en los datos enviados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ValidationErrorResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El usuario no puede crearse porque el email ya existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ErrorResponseDTO.class),
                            examples = {@ExampleObject(
                                    name = "Email duplicado",
                                    value = """
                                            {
                                              "status": 409,
                                              "message": "El email ya está registrado",
                                              "timestamp": "2026-06-14T21:00:42.290Z"
                                            }
                                            """
                            )}
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ErrorResponseDTO.class),
                            examples = {@ExampleObject(
                                    name = "Error interno",
                                    value = """
                                            {
                                              "status": 500,
                                              "message": "Error interno del servidor",
                                              "timestamp": "2026-06-14T21:00:42.290Z"
                                            }
                                            """
                            )}
                    )
            )
    })
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioCreateRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.create(requestDTO));
    }
}
