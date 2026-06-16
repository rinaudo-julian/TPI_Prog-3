package com.utn.backend.service.impl;

import com.utn.backend.dto.UsuarioCreateRequestDTO;
import com.utn.backend.dto.UsuarioEditRequestDTO;
import com.utn.backend.dto.UsuarioResponseDTO;
import com.utn.backend.exception.BusinessException;
import com.utn.backend.enums.Rol;
import com.utn.backend.mappers.UsuarioMapper;
import com.utn.backend.model.Usuario;
import com.utn.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioResponseDTO create(UsuarioCreateRequestDTO usuarioRequestDTO) {
        if (usuarioRepository.existsByEmail(usuarioRequestDTO.email())) {
            throw new IllegalStateException("El email ya está registrado");
        }

        Usuario usuario = usuarioMapper.toEntity(usuarioRequestDTO);
        usuario.setContrasena(passwordEncoder.encode(usuarioRequestDTO.password()));
        usuario.setRol(Rol.USUARIO);

        usuario = usuarioRepository.save(usuario);

        return usuarioMapper.toDto(usuario);
    }

    public List<UsuarioResponseDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDto)
                .toList();
    }

    public UsuarioResponseDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findByIdOrThrow(id);
        return usuarioMapper.toDto(usuario);
    }

    public UsuarioResponseDTO update(Long id, UsuarioEditRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findByIdOrThrow(id);

        if (requestDTO.getEmail() != null
                && usuarioRepository.existsByEmailAndIdNot(requestDTO.getEmail(), id)) {
            throw new BusinessException("El email ya está registrado");
        }

        requestDTO.applyTo(usuario);

        if (requestDTO.getPassword() != null) {
            usuario.setContrasena(passwordEncoder.encode(requestDTO.getPassword()));
        }

        usuario = usuarioRepository.save(usuario);

        return usuarioMapper.toDto(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }
}
