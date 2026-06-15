package com.utn.backend.controller;

import com.utn.backend.model.Usuario;
import com.utn.backend.enums.Rol;
import com.utn.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        void findAllShouldReturnActiveUsersAndExcludeDeletedOnes() throws Exception {
                Usuario admin = new Usuario();
                admin.setNombre("Admin");
                admin.setApellido("Admin");
                admin.setEmail("admin@admin.com");
                admin.setContrasena("hashed-admin");
                admin.setRol(Rol.ADMIN);
                usuarioRepository.save(admin);

                Usuario activo = new Usuario();
                activo.setNombre("Juan");
                activo.setApellido("Perez");
                activo.setEmail("juan.perez@mail.com");
                activo.setContrasena("hashed-user");
                activo.setRol(Rol.USUARIO);
                usuarioRepository.save(activo);

                Usuario eliminado = new Usuario();
                eliminado.setNombre("Borrado");
                eliminado.setApellido("User");
                eliminado.setEmail("borrado@mail.com");
                eliminado.setContrasena("hashed-deleted");
                eliminado.setRol(Rol.USUARIO);
                eliminado.setEliminado(true);
                usuarioRepository.save(eliminado);

                mockMvc.perform(get("/usuarios"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].id").exists())
                                .andExpect(jsonPath("$[0].nombre").exists())
                                .andExpect(jsonPath("$[0].apellido").exists())
                                .andExpect(jsonPath("$[0].mail").exists())
                                .andExpect(jsonPath("$[0].celular").hasJsonPath())
                                .andExpect(jsonPath("$[0].rol").exists())
                                .andExpect(jsonPath("$[*].mail", hasItems("admin@admin.com",
                                                "juan.perez@mail.com")))
                                .andExpect(jsonPath("$[*].mail", not(hasItems("borrado@mail.com"))))
                                .andReturn();
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

        @Test
        void createShouldReturn409WhenEmailAlreadyExists() throws Exception {
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
                                .andExpect(status().isCreated());

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
}
