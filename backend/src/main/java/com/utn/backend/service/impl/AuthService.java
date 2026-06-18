package com.utn.backend.service.impl;

import com.utn.backend.dto.LoginRequestDTO;
import com.utn.backend.dto.UsuarioResponseDTO;
import com.utn.backend.exception.UnauthorizedException;
import com.utn.backend.mappers.UsuarioMapper;
import com.utn.backend.model.Usuario;
import com.utn.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String INVALID_CREDENTIALS_MESSAGE = "Credenciales inválidas";

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioResponseDTO login(LoginRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findByEmailAndEliminadoFalse(requestDTO.email())
                .orElseThrow(() -> new UnauthorizedException(INVALID_CREDENTIALS_MESSAGE));

        if (!passwordEncoder.matches(requestDTO.password(), usuario.getContrasena())) {
            throw new UnauthorizedException(INVALID_CREDENTIALS_MESSAGE);
        }

        return usuarioMapper.toDto(usuario);
    }
}
