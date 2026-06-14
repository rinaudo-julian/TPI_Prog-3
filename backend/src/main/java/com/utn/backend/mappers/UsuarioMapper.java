package com.utn.backend.mappers;

import com.utn.backend.dto.UsuarioCreateRequestDTO;
import com.utn.backend.dto.UsuarioResponseDTO;
import com.utn.backend.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {
    @Mapping(target = "contrasena", ignore = true)
    @Mapping(target = "rol", ignore = true)
    Usuario toEntity(UsuarioCreateRequestDTO usuarioCreateRequestDTO);

    @Mapping(target = "mail", source = "email")
    UsuarioResponseDTO toDto(Usuario usuario);
}
