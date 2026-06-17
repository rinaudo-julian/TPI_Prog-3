package com.utn.backend.controller;

import com.utn.backend.dto.PedidoCreateRequestDTO;
import com.utn.backend.dto.PedidoEditRequestDTO;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Operaciones para crear, listar, actualizar y eliminar pedidos")
public class PedidoController {
  private final PedidoService pedidoService;

  @GetMapping
  @Operation(summary = "Listar pedidos", description = "Devuelve todos los pedidos activos del sistema con sus detalles y productos asociados.")
  @ApiResponse(responseCode = "200", description = "Pedidos listados correctamente")
  public ResponseEntity<List<PedidoResponseDTO>> findAll() {
    return ResponseEntity.ok(pedidoService.findAll());
  }

  @GetMapping("/{id}")
  @Operation(summary = "Obtener pedido por id", description = "Devuelve un pedido específico con sus detalles y productos asociados.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pedido encontrado correctamente"),
      @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content(mediaType = "application/json", examples = {
          @ExampleObject(name = "Pedido inexistente", value = """
              {
                "status": 404,
                "message": "Recurso no encontrado",
                "timestamp": "2026-06-14T21:00:42.290Z"
              }
              """)
      }))
  })
  public ResponseEntity<PedidoResponseDTO> findById(@PathVariable Long id) {
    return ResponseEntity.ok(pedidoService.findById(id));
  }

  @GetMapping("/usuario/{id}")
  @Operation(summary = "Listar pedidos por usuario", description = "Devuelve el historial de pedidos del usuario especificado.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pedidos del usuario listados correctamente"),
      @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(mediaType = "application/json", examples = {
          @ExampleObject(name = "Usuario inexistente", value = """
              {
                "status": 404,
                "message": "Recurso no encontrado",
                "timestamp": "2026-06-14T21:00:42.290Z"
              }
              """)
      }))
  })
  public ResponseEntity<List<PedidoResponseDTO>> findByUsuarioId(@PathVariable Long id) {
    return ResponseEntity.ok(pedidoService.findByUsuarioId(id));
  }

  @PostMapping
  @Operation(summary = "Crear pedido", description = "Crea un pedido validando stock, disponibilidad y calculando totales automáticamente.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Pedido creado correctamente"),
      @ApiResponse(responseCode = "400", description = "Datos inválidos o validación de negocio fallida", content = @Content(mediaType = "application/json", examples = {
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
      })),
      @ApiResponse(responseCode = "404", description = "Usuario o producto no encontrado", content = @Content(mediaType = "application/json", examples = {
          @ExampleObject(name = "Entidad inexistente", value = """
              {
                "status": 404,
                "message": "Entidad con id 999 no encontrado",
                "timestamp": "2026-06-14T21:00:42.290Z"
              }
              """) }))
  })
  public ResponseEntity<PedidoResponseDTO> create(@Valid @RequestBody PedidoCreateRequestDTO requestDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.save(requestDTO));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Actualizar pedido", description = "Actualiza parcialmente el estado y la forma de pago de un pedido existente.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Pedido actualizado correctamente"),
      @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content(mediaType = "application/json", examples = {
          @ExampleObject(name = "Pedido inexistente", value = """
              {
                "status": 404,
                "message": "Recurso no encontrado",
                "timestamp": "2026-06-14T21:00:42.290Z"
              }
              """)
      })),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = {
          @ExampleObject(name = "Error interno", value = """
              {
                "status": 500,
                "message": "Error interno del servidor",
                "timestamp": "2026-06-14T21:00:42.290Z"
              }
              """)
      }))
  })
  public ResponseEntity<PedidoResponseDTO> update(@PathVariable Long id, @RequestBody PedidoEditRequestDTO requestDTO) {
    return ResponseEntity.ok(pedidoService.update(id, requestDTO));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Eliminar pedido", description = "Realiza una eliminacion logica de un pedido existente.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Pedido eliminado correctamente"),
      @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content(mediaType = "application/json", examples = {
          @ExampleObject(name = "Pedido inexistente", value = """
              {
                "status": 404,
                "message": "Recurso no encontrado",
                "timestamp": "2026-06-14T21:00:42.290Z"
              }
              """)
      })),
      @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(mediaType = "application/json", examples = {
          @ExampleObject(name = "Error interno", value = """
              {
                "status": 500,
                "message": "Error interno del servidor",
                "timestamp": "2026-06-14T21:00:42.290Z"
              }
              """)
      }))
  })
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    pedidoService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
