package com.utn.backend.repository;

import com.utn.backend.model.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
