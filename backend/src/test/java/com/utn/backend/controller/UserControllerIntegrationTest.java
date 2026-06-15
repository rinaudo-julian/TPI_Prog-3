package com.utn.backend.controller;

import com.utn.backend.model.Usuario;
import com.utn.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
    }

    @Test
    void createShouldReturn201WhenUserIsCreated() throws Exception {
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Perez"))
                .andExpect(jsonPath("$.mail").value("juan.perez@mail.com"))
                .andExpect(jsonPath("$.celular").value("+5491122334455"))
                .andExpect(jsonPath("$.rol").value("USUARIO"));

        Usuario usuario = usuarioRepository.findAll().stream()
                .filter(item -> "juan.perez@mail.com".equals(item.getEmail()))
                .findFirst()
                .orElseThrow();

        assertEquals("Juan", usuario.getNombre());
        assertEquals("Perez", usuario.getApellido());
        assertEquals("juan.perez@mail.com", usuario.getEmail());
        assertEquals("+5491122334455", usuario.getCelular());
        assertTrue(passwordEncoder.matches("Secreta123", usuario.getContrasena()));
    }
}
