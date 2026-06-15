package com.utn.backend.controller;

import com.utn.backend.exception.GlobalHandlerException;
import com.utn.backend.service.impl.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@WebMvcTest(UsuarioController.class)
@Import(GlobalHandlerException.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UsuarioService usuarioService;

  @Test
  void createShouldReturn400WhenEmailIsInvalid() throws Exception {
    mockMvc.perform(post("/usuarios")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "nombre": "Juan",
              "apellido": "Perez",
              "email": "mail-invalido",
              "celular": "+5491122334455",
              "password": "Secreta123"
            }
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Error de validación"))
        .andExpect(jsonPath("$.errors[0]").value("email: El email debe tener un formato válido"));
  }

  @Test
  void createShouldReturn400WhenPasswordIsTooShort() throws Exception {
    mockMvc.perform(post("/usuarios")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "nombre": "Juan",
              "apellido": "Perez",
              "email": "juan.perez@mail.com",
              "celular": "+5491122334455",
              "password": "123"
            }
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Error de validación"))
        .andExpect(jsonPath("$.errors[0]").value("password: La contraseña debe tener al menos 6 caracteres"));
  }

  @Test
  void createShouldReturn409WhenEmailAlreadyExists() throws Exception {
    when(usuarioService.create(org.mockito.ArgumentMatchers.any()))
        .thenThrow(new IllegalStateException("El email ya está registrado"));

    mockMvc.perform(post("/usuarios")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "nombre": "Juan",
              "apellido": "Perez",
              "email": "juan.perez@mail.com",
              "celular": "+5491122334455",
              "password": "Secreta123"
            }
            """))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.message").value("El email ya está registrado"));
  }

  @Test
  void createShouldReturn400WhenNameIsBlank() throws Exception {
    mockMvc.perform(post("/usuarios")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "nombre": "",
              "apellido": "Perez",
              "email": "juan.perez@mail.com",
              "celular": "+5491122334455",
              "password": "Secreta123"
            }
            """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.errors[0]").value("nombre: El nombre debe tener entre 2 y 50 caracteres"));
  }
}
