package com.utn.backend.controller;

import com.utn.backend.dto.CategoriaCreateRequestDTO;
import com.utn.backend.dto.CategoriaResponseDTO;
import com.utn.backend.service.impl.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Operaciones para crear y administrar categorias")
public class CategoriaController {
    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(
            summary = "Listar categorias",
            description = "Retorna todas las categorias activas del sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categorias listadas correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoriaResponseDTO.class))
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
    public ResponseEntity<List<CategoriaResponseDTO>> findAll() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @PostMapping
    @Operation(
            summary = "Crear categoria",
            description = "Registra una nueva categoria en el sistema y devuelve la categoria creada."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Categoria creada correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = CategoriaResponseDTO.class)
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
                    description = "La categoria no puede crearse porque el nombre ya existe",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ErrorResponseDTO.class),
                            examples = {@ExampleObject(
                                    name = "Nombre de categoria duplicado",
                                    value = """
                                            {
                                              "status": 409,
                                              "message": "Ya existe una categoría con ese nombre",
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
    public ResponseEntity<CategoriaResponseDTO> create(@Valid @RequestBody CategoriaCreateRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.create(requestDTO));
    }

}
