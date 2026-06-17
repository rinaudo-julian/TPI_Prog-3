package com.utn.backend.controller;

import com.utn.backend.dto.PedidoCreateRequestDTO;
import com.utn.backend.dto.PedidoResponseDTO;
import com.utn.backend.service.impl.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Operaciones para crear pedidos")
public class PedidoController {
    private final PedidoService pedidoService;

    @PostMapping
    @Operation(
            summary = "Crear pedido",
            description = "Crea un pedido validando stock, disponibilidad y calculando totales automáticamente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado correctamente"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o validación de negocio fallida",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Estado obligatorio", value = """
                                            {
                                              "status": 400,
                                              "message": "Error de validación",
                                              "timestamp": "2026-06-14T21:00:42.290Z",
                                              "errors": ["estado: El estado es obligatorio"]
                                            }
                                            """),
                                    @ExampleObject(name = "Stock insuficiente", value = """
                                            {
                                              "status": 400,
                                              "message": "Stock insuficiente para 'Producto A'. Disponible: 5, Solicitado: 10",
                                              "timestamp": "2026-06-14T21:00:42.290Z"
                                            }
                                            """)
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario o producto no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {@ExampleObject(name = "Entidad inexistente", value = """
                                    {
                                      "status": 404,
                                      "message": "Entidad con id 999 no encontrado",
                                      "timestamp": "2026-06-14T21:00:42.290Z"
                                    }
                                    """)}
                    )
            )
    })
    public ResponseEntity<PedidoResponseDTO> create(@Valid @RequestBody PedidoCreateRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.save(requestDTO));
    }
}
