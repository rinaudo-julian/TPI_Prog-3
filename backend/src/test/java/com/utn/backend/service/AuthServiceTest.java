package com.utn.backend.service;

import com.utn.backend.dto.LoginRequestDTO;
import com.utn.backend.dto.UsuarioResponseDTO;
import com.utn.backend.enums.Rol;
import com.utn.backend.exception.UnauthorizedException;
import com.utn.backend.mappers.UsuarioMapper;
import com.utn.backend.model.Usuario;
import com.utn.backend.repository.UsuarioRepository;
import com.utn.backend.service.impl.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginShouldThrowUnauthorizedWhenEmailDoesNotExist() {
        LoginRequestDTO request = new LoginRequestDTO("noexiste@mail.com", "Secreta123");

        when(usuarioRepository.findByEmailAndEliminadoFalse(request.email())).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> authService.login(request));

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(usuarioRepository).findByEmailAndEliminadoFalse(request.email());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(usuarioMapper, never()).toDto(any());
    }

    @Test
    void loginShouldThrowUnauthorizedWhenPasswordIsInvalid() {
        LoginRequestDTO request = new LoginRequestDTO("juan.perez@mail.com", "ClaveInvalida123");

        Usuario usuario = createUsuario(request.email(), "hashed-password");

        when(usuarioRepository.findByEmailAndEliminadoFalse(request.email())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(request.password(), usuario.getContrasena())).thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> authService.login(request));

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(usuarioRepository).findByEmailAndEliminadoFalse(request.email());
        verify(passwordEncoder).matches(request.password(), usuario.getContrasena());
        verify(usuarioMapper, never()).toDto(any());
    }

    @Test
    void loginShouldReturnUserDtoWhenCredentialsAreValid() {
        LoginRequestDTO request = new LoginRequestDTO("juan.perez@mail.com", "Secreta123");

        Usuario usuario = createUsuario(request.email(), "hashed-password");
        UsuarioResponseDTO response = new UsuarioResponseDTO(
                1L,
                "Juan",
                "Perez",
                request.email(),
                "+5491122334455",
                Rol.USUARIO
        );

        when(usuarioRepository.findByEmailAndEliminadoFalse(request.email())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(request.password(), usuario.getContrasena())).thenReturn(true);
        when(usuarioMapper.toDto(usuario)).thenReturn(response);

        UsuarioResponseDTO result = authService.login(request);

        assertEquals(response, result);
        verify(usuarioRepository).findByEmailAndEliminadoFalse(request.email());
        verify(passwordEncoder).matches(request.password(), usuario.getContrasena());
        verify(usuarioMapper).toDto(usuario);
    }

    private Usuario createUsuario(String email, String contrasena) {
        Usuario usuario = new Usuario();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        usuario.setEmail(email);
        usuario.setCelular("+5491122334455");
        usuario.setContrasena(contrasena);
        usuario.setRol(Rol.USUARIO);
        return usuario;
    }
}
