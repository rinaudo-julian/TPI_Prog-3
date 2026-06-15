package com.utn.backend.service;

import com.utn.backend.dto.UsuarioCreateRequestDTO;
import com.utn.backend.dto.UsuarioResponseDTO;
import com.utn.backend.enums.Rol;
import com.utn.backend.mappers.UsuarioMapper;
import com.utn.backend.model.Usuario;
import com.utn.backend.repository.UsuarioRepository;
import com.utn.backend.service.impl.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void createWhenEmailDoesNotExistShouldSaveUserWithHashedPassword() {
        UsuarioCreateRequestDTO request = new UsuarioCreateRequestDTO(
                "Juan",
                "Perez",
                "juan.perez@mail.com",
                "+5491122334455",
                "Secreta123"
        );

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setEmail(request.email());
        usuario.setCelular(request.celular());

        UsuarioResponseDTO response = new UsuarioResponseDTO(
                1L,
                request.nombre(),
                request.apellido(),
                request.email(),
                request.celular(),
                Rol.USUARIO
        );

        when(usuarioRepository.existsByEmail(request.email())).thenReturn(false);
        when(usuarioMapper.toEntity(request)).thenReturn(usuario);
        when(passwordEncoder.encode(request.password())).thenReturn("hashed-password");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioMapper.toDto(any(Usuario.class))).thenReturn(response);

        UsuarioResponseDTO result = usuarioService.create(request);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        verify(usuarioRepository).existsByEmail(request.email());
        verify(passwordEncoder).encode(request.password());

        Usuario savedUsuario = usuarioCaptor.getValue();
        assertEquals(request.nombre(), savedUsuario.getNombre());
        assertEquals(request.apellido(), savedUsuario.getApellido());
        assertEquals(request.email(), savedUsuario.getEmail());
        assertEquals(request.celular(), savedUsuario.getCelular());
        assertEquals("hashed-password", savedUsuario.getContrasena());
        assertEquals(Rol.USUARIO, savedUsuario.getRol());
        assertEquals(response, result);
    }

    @Test
    void createWhenEmailAlreadyExistsShouldThrowIllegalStateException() {
        UsuarioCreateRequestDTO request = new UsuarioCreateRequestDTO(
                "Juan",
                "Perez",
                "juan.perez@mail.com",
                "+5491122334455",
                "Secreta123"
        );

        when(usuarioRepository.existsByEmail(request.email())).thenReturn(true);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> usuarioService.create(request)
        );

        assertEquals("El email ya está registrado", exception.getMessage());
        verify(usuarioRepository).existsByEmail(request.email());
        verify(usuarioRepository, never()).save(any());
        verify(usuarioMapper, never()).toEntity(any());
        verify(passwordEncoder, never()).encode(any());
    }
}
