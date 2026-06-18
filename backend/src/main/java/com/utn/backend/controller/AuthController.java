package com.utn.backend.controller;

import com.utn.backend.dto.LoginRequestDTO;
import com.utn.backend.dto.UsuarioResponseDTO;
import com.utn.backend.service.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Operaciones de autenticacion")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion", description = "Valida credenciales y devuelve los datos publicos del usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "400", description = "Datos de ingreso invalidos", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Email invalido", value = """
                            {
                              "status": 400,
                              "message": "Error de validación",
                              "timestamp": "2026-06-14T21:00:42.290Z",
                              "errors": ["email: El email debe tener un formato válido"]
                            }
                            """)
            })),
            @ApiResponse(responseCode = "401", description = "Credenciales invalidas", content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "Usuario o password invalidos", value = """
                            {
                              "status": 401,
                              "message": "Credenciales inválidas",
                              "timestamp": "2026-06-14T21:00:42.290Z"
                            }
                            """)
            }))
    })
    public ResponseEntity<UsuarioResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        return ResponseEntity.ok(authService.login(requestDTO));
    }
}
