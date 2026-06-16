package com.utn.backend.controller;

import com.utn.backend.dto.ProductoCreateRequestDTO;
import com.utn.backend.dto.ProductoResponseDTO;
import com.utn.backend.service.impl.ProductoService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Operaciones para crear y administrar productos")
public class ProductoController {
    private final ProductoService productoService;

    @GetMapping
    @Operation(
            summary = "Listar productos",
            description = "Retorna todos los productos activos del sistema incluyendo su categoria."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Productos listados correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ProductoResponseDTO.class))
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
    public ResponseEntity<List<ProductoResponseDTO>> findAll() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/categoria/{id}")
    @Operation(
            summary = "Listar productos por categoria",
            description = "Retorna todos los productos activos de una categoria incluyendo su categoria."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Productos listados correctamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ProductoResponseDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoria no encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ErrorResponseDTO.class),
                            examples = {@ExampleObject(
                                    name = "Categoria inexistente",
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "La categoría no existe",
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
    public ResponseEntity<List<ProductoResponseDTO>> findByCategoriaId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.findByCategoriaId(id));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener producto por id",
            description = "Retorna un producto activo por su identificador incluyendo su categoria."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ProductoResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ErrorResponseDTO.class),
                            examples = {@ExampleObject(
                                    name = "Producto inexistente",
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
    public ResponseEntity<ProductoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar producto",
            description = "Realiza una eliminacion logica de un producto existente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Producto eliminado correctamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ErrorResponseDTO.class),
                            examples = {@ExampleObject(
                                    name = "Producto inexistente",
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
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(
            summary = "Crear producto",
            description = "Registra un nuevo producto en el sistema y devuelve el producto creado con su categoria."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Producto creado correctamente"
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
                    description = "Categoria no encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.utn.backend.dto.ErrorResponseDTO.class),
                            examples = {@ExampleObject(
                                    name = "Categoria inexistente",
                                    value = """
                                            {
                                              "status": 404,
                                              "message": "La categoría no existe",
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
    public ResponseEntity<ProductoResponseDTO> create(@Valid @RequestBody ProductoCreateRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.save(requestDTO));
    }
}
